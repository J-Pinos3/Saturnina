package com.example.saturninaapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.drawerlayout.widget.DrawerLayout
import com.example.saturninaapp.R
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.models.DetailXUser
import com.example.saturninaapp.models.ProductOrderInfo
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileReader
import java.io.InputStream
import java.lang.Exception
import javax.xml.transform.stream.StreamResult


class CompleteSaleActivity : AppCompatActivity() {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_complete_sale: NavigationView
    private lateinit var userOwner: DetailXUser
    private var bearerToken = ""

    lateinit var btnAcceptSale: AppCompatButton
    private var finalListOfProducts = mutableListOf<DetailProduct>()
    private lateinit var etOrderAddress: EditText
    private lateinit var etOrderDescription: EditText
    private lateinit var tvTotalSalePrice: TextView
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123



    private val pickImage = registerForActivityResult( ActivityResultContracts.StartActivityForResult() ){
        if(it.resultCode == Activity.RESULT_OK){
            val data = it.data
            val imgUri = data?.data

            CoroutineScope(Dispatchers.IO).launch {

                if(imgUri != null){
                    val file = uriToFile(imgUri)
                    if(file != null && file.exists()){
                        val totalValue = getTotalValueOfCart()
                        val idcounterList = getListWithIdCounter(finalListOfProducts)

                        if (ContextCompat.checkSelfPermission(
                                this@CompleteSaleActivity,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@CompleteSaleActivity,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                            )
                        }else{
                            addDataToOrder(bearerToken, userOwner.id, totalValue, userOwner.nombre, userOwner.apellido, etOrderAddress.text.toString(),
                                userOwner.telefono, etOrderDescription.text.toString(), file, idcounterList)
                        }


                    }
                }

            }

        }
    }

    private lateinit var cartSalesItemsCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_sale)
        initUI()
        val totalItems: String? = intent.extras?.getString("TOTAL_CART_ITEMS")
        val userToken = intent.extras?.getString("USER_TOKENTO_PROFILE")
        val cartKey: String = "car_items"
        bearerToken = "Bearer $userToken"
        if (totalItems != null) {
            showTotalCartItems(totalItems)
        }

        loadItemsFromFiles(cartKey)
        CoroutineScope(Dispatchers.IO).launch {
            userOwner = bringUserData(bearerToken)
        }

        setTotalValueView()
        btnAcceptSale.setOnClickListener {
            val selectBillImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            pickImage.launch(selectBillImage)
        }


        //navigation
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav_view_complete_sale.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_item_one ->{
                    val intent = Intent(this, IntroDashboardNews::class.java)
                    intent.putExtra("USER_TOKEN", userToken)
                    startActivity(intent)
                }

                R.id.nav_item_two ->{
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("USER_TOKEN", userToken)
                    startActivity(intent)
                }

                R.id.nav_item_three ->{
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("USER_TOKEN_PROFILE", userToken)
                    startActivity(intent)
                }

                R.id.nav_item_four ->{
                    //NOSOTROS
                }

                R.id.nav_item_five ->{
                    val intent = Intent(this, ManagementOptionsActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_item_six ->{
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }

            true
        }

    }//ON CREATE


    private fun initUI() {
        //navigation
        drawer = findViewById(R.id.drawerLayoutCompleteSale)
        nav_view_complete_sale = findViewById(R.id.nav_view_complete_sale)

        //cart items count
        cartSalesItemsCount = findViewById(R.id.action_cart_count)

        //order address
        etOrderAddress = findViewById(R.id.etOrderAddress)

        //order description
        etOrderDescription = findViewById(R.id.etOrderDescription)

        //total value of sale
        tvTotalSalePrice = findViewById(R.id.tvTotalSalePrice)

        //send order button / accept sale
        btnAcceptSale = findViewById(R.id.btnAcceptSale)
    }

    private fun showTotalCartItems(numberOfitems: String){
        cartSalesItemsCount.text = numberOfitems
    }

    private fun loadItemsFromFiles(key: String){
        var sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
        val gson = Gson()

        val jsonString = sharedPreferences.getString(key, "")
        val type = object : TypeToken< MutableList<DetailProduct> >() {}.type
        finalListOfProducts = (gson.fromJson(jsonString, type)  ?: emptyList<DetailProduct>()) as MutableList<DetailProduct>

    }

    private fun setTotalValueView(){
        tvTotalSalePrice.text = getTotalValueOfCart().toString()
    }

    //final price of purchase
    private fun getTotalValueOfCart(): Double{
        var suma: Double = 0.0
        for(k in finalListOfProducts){
            suma += k.precio * k.contador
        }
        return suma
    }

    suspend fun bringUserData(bearerToken: String): DetailXUser{
        val currentUser: DetailXUser
        try {

            val retrofitGetProfile = RetrofitHelper.consumeAPI.getUserProfile(bearerToken)
            if(retrofitGetProfile.isSuccessful){
                var userResponseProfile = retrofitGetProfile.body()

                withContext(Dispatchers.Main){
                    currentUser = DetailXUser(
                        userResponseProfile?.detail?.apellido.toString(),
                        userResponseProfile?.detail?.email.toString(),
                        userResponseProfile?.detail?.id.toString(),
                        userResponseProfile?.detail?.nombre.toString(),
                        userResponseProfile?.detail?.telefono.toString(),
                    )
                }

                return currentUser
            }else{
                runOnUiThread {
                    Log.e("COULDN'T BRING USER DATA", "${retrofitGetProfile.code()} --- ${retrofitGetProfile.errorBody()?.toString()}")
                }
            }
        }catch (e:Exception){
            println("Error al cargar el usuario para generar la orden " + e.printStackTrace())
        }

        return DetailXUser()
    }


    suspend fun addDataToOrder(bearerToken: String, userId: String, priceOrder: Double, nombre: String,
        apellido: String, direccion:String, telefono: String, descripcion: String, image: File,  productsData: List<ProductOrderInfo>
    ){
        try {
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), image)
            val filepart = MultipartBody.Part.createFormData(
                "transfer_image",
                image.name,
                requestFile
            )

            val listPart = mutableListOf<MultipartBody.Part>()
            for(product in productsData){
                val productPart = product.toMultipart("products")
                listPart.add(productPart)
            }

            val userIdRequestBody = createPartFromString(userId)
            val priceOrderRequestBody = createPartFromString(priceOrder.toString())
            val nombreRequestBody = createPartFromString(nombre)
            val apellidoRequestBody = createPartFromString(apellido)
            val direccionRequestBody = createPartFromString(direccion)
            val telefonoRequestBody = createPartFromString(telefono)
            val descripcionRequestBody = createPartFromString(descripcion)

            val retrofitSendOrder = RetrofitHelper.consumeAPI.createOrder(
                bearerToken, userIdRequestBody,
                priceOrderRequestBody, nombreRequestBody, apellidoRequestBody,
                direccionRequestBody, telefonoRequestBody,
                descripcionRequestBody, filepart , listPart )


            if(retrofitSendOrder.isSuccessful){
                val jsonResponse = retrofitSendOrder.body()
                withContext(Dispatchers.Main){
                    Log.i("SEND ORDER", "ORDER SENT SUCCESSFULLY: $jsonResponse")
                }

            }else{
                runOnUiThread {
                    val msg = retrofitSendOrder.errorBody().toString()
                    Log.i("SEND ORDER ERROR", "ORDER COULDN'T BE SENT: ${retrofitSendOrder.errorBody().toString()} -- $msg")
                }
            }

        }catch (e: Exception){
            Log.e("SENDING ORDER", "ERROR WHILE SENDING ORDER ${e.message}")
        }
    }


    private fun getListWithIdCounter(listOfProducts: MutableList<DetailProduct>): List<ProductOrderInfo> {
        val finalList = mutableListOf<ProductOrderInfo>()
        for( k in listOfProducts.indices ){
            val currentProduct = ProductOrderInfo(
                id = listOfProducts[k].id,
                contador = listOfProducts[k].contador
            )
            finalList.add(currentProduct)
        }

        return finalList
    }

//    private fun Uri.toMultipart(partName:String): MultipartBody.Part{
//        val file = File(this.path ?: "")
//        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
//        return MultipartBody.Part.createFormData( partName, file.name, requestFile )
//    }

    private fun createPartFromString(value: String): RequestBody {
        return RequestBody.create(MultipartBody.FORM, value)
    }

    private fun ProductOrderInfo.toMultipart(partName: String): MultipartBody.Part{
        val json = Gson().toJson(this)
        val requestPart = RequestBody.create( MediaType.parse("application/json"), json )
        return MultipartBody.Part.createFormData( partName,null,requestPart )
    }


    private fun uriToFile(uri: Uri): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex: Int? = cursor?.getColumnIndex(filePathColumn[0])
        val filePath: String? = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return filePath?.let { File(it) }
    }
}