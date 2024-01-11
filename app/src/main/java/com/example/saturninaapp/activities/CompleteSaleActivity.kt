package com.example.saturninaapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.saturninaapp.R
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.models.DetailXUser
import com.example.saturninaapp.models.ProductOrderInfo
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.Exception


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
    private var user_id = ""
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: Int = 123
    private var cartKey :String = ""

    private val MIN_LENGTH_DESCRIPTION = 10
    private val MAX_LENGTH_DESCRIPTION = 100

    var completSaleTextWatcher = object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val description = etOrderDescription.text.toString()
            val address = etOrderAddress.text.toString()

            disableClickAcceptSale(description, address)

        }

    }


    private val pickImage = registerForActivityResult( ActivityResultContracts.StartActivityForResult() ){
        if(it.resultCode == Activity.RESULT_OK){
            val data = it.data
            val imgUri = data?.data

            CoroutineScope(Dispatchers.IO).launch {

                if(imgUri != null){
                    val file = uriToFile(imgUri)
                    if(file != null && file.exists()){
                        val totalValue = getTotalValueOfCart()
                        val idcounterList = getListWithIdCounterSizeColor(finalListOfProducts)
                        Log.i("FINAL LIST", "$idcounterList")
                        addDataToOrder(bearerToken, user_id, totalValue, idcounterList, userOwner.nombre, userOwner.apellido, etOrderAddress.text.toString(),
                            userOwner.email, userOwner.telefono, etOrderDescription.text.toString(), file, cartKey)

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
        val user_rol = intent.extras?.getString("USER_ROL")
        user_id = intent.extras?.getString("USER_ID").toString()
        cartKey = "car_items"
        bearerToken = "Bearer $userToken"
        if (totalItems != null) {
            showTotalCartItems(totalItems)
        }

        loadItemsFromFiles(cartKey)
        CoroutineScope(Dispatchers.IO).launch {
            bringUserData(bearerToken, user_id)
        }

        setTotalValueView()
        btnAcceptSale.setOnClickListener {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE  )
            val selectBillImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            pickImage.launch(selectBillImage)

        }


        etOrderAddress.addTextChangedListener(completSaleTextWatcher)
        etOrderDescription.addTextChangedListener(completSaleTextWatcher)

        etOrderDescription.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                validateUserDescriptionInput()
            }
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
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.nav_item_two ->{
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("USER_TOKEN", userToken)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.nav_item_three ->{
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("USER_TOKEN_PROFILE", userToken)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.nav_item_four ->{
                    //NOSOTROS
                }

                R.id.nav_item_five ->{
                    val intent = Intent(this, ManagementOptionsActivity::class.java)
                    intent.putExtra("USER_TOKEN", userToken)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if( toggle.onOptionsItemSelected(item) ){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

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

    suspend fun bringUserData(bearerToken: String, user_id: String){

        try {

            val retrofitGetProfile = RetrofitHelper.consumeAPI.getUserProfile(bearerToken)
            if(retrofitGetProfile.isSuccessful){
                var userResponseProfile = retrofitGetProfile.body()

                withContext(Dispatchers.Main){


                    userOwner = DetailXUser(  userResponseProfile?.detail?.apellido.toString(),
                        userResponseProfile?.detail?.email.toString(),
                        user_id,
                        userResponseProfile?.detail?.nombre.toString(),
                        userResponseProfile?.detail?.telefono.toString(),
                        userResponseProfile?.detail?.token.toString()
                    )
                    Log.i("USER DATA FOR ORDER", "${userOwner.nombre} + ${userOwner.telefono}")
                }


            }else{
                runOnUiThread {
                    Log.e("COULDN'T BRING USER DATA", "${retrofitGetProfile.code()} --- ${retrofitGetProfile.errorBody()?.toString()}")
                }
            }
        }catch (e:Exception){
            println("Error al cargar el usuario para generar la orden " + e.printStackTrace())
        }


    }


    suspend fun addDataToOrder(bearerToken: String, userId: String, priceOrder: Double, productsData: List<ProductOrderInfo>, nombre: String,
        apellido: String, direccion:String, email: String, telefono: String, descripcion: String, image: File, key: String
    ){
        try {
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), image)
            val filepart = MultipartBody.Part.createFormData(
                "transfer_image",
                image.name,
                requestFile
            )
            Log.i("iimage loaded:","${image.absoluteFile}")

//            val listPart = mutableListOf<MultipartBody.Part>()
//            for(product in productsData){
//                val productPart = product.toMultipart("products")
//                listPart.add(productPart)
//            }

            //val gson = Gson()
            val dataJson = JsonObject()

            val productsArray = JsonArray()
            for(product in productsData){
                val productObject = JsonObject()
                productObject.addProperty("id_producto", product.id)
                productObject.addProperty("cantidad", product.contador)
                productObject.addProperty("talla", product.talla)
                productObject.addProperty("color", product.color)
                productsArray.add(productObject)
            }

            dataJson.addProperty("user_id", userId)
            dataJson.addProperty("price_order", priceOrder)
            dataJson.add("products", productsArray)
            dataJson.addProperty("nombre", nombre)
            dataJson.addProperty("apellido", apellido)
            dataJson.addProperty("direccion", direccion)
            dataJson.addProperty("email", email)
            dataJson.addProperty("telefono", telefono)
            dataJson.addProperty("descripcion", descripcion)
            val dataBody = RequestBody.create(MediaType.parse("application/json"), dataJson.toString())
            val retrofitSendOrder = RetrofitHelper.consumeAPI.createOrder(
                bearerToken, dataBody, filepart )

//            val userIdRequestBody = createPartFromString(userId)
//            val priceOrderRequestBody = createPartFromString(priceOrder.toString())
//            val nombreRequestBody = createPartFromString(nombre)
//            val apellidoRequestBody = createPartFromString(apellido)
//            val direccionRequestBody = createPartFromString(direccion)
//            val telefonoRequestBody = createPartFromString(telefono)
//            val emailRequestBody = createPartFromString(email)
//            val descripcionRequestBody = createPartFromString(descripcion)
//
//            val retrofitSendOrder = RetrofitHelper.consumeAPI.createOrder(
//                bearerToken, userIdRequestBody,
//                priceOrderRequestBody, listPart, nombreRequestBody, apellidoRequestBody,
//                direccionRequestBody, emailRequestBody,telefonoRequestBody,
//                descripcionRequestBody, filepart  )


            if(retrofitSendOrder.isSuccessful){
                Log.i("SEND ORDER", "AQUIII")
                val jsonResponse = retrofitSendOrder.body()
                withContext(Dispatchers.Main){
                    Log.i("SEND ORDER", "ORDER SENT SUCCESSFULLY: $jsonResponse")
                    clearCart(key)
                }

            }else{
                runOnUiThread {
                    Log.i("SEND ORDER ERROR", "ORDER COULDN'T BE SENT: ${retrofitSendOrder.errorBody()?.string()}")
                }
            }

        }catch (e: Exception){
            Log.e("SENDING ORDER", "ERROR WHILE SENDING ORDER ${e.message} ---**- $e")
        }
    }


    private fun getListWithIdCounterSizeColor(listOfProducts: MutableList<DetailProduct>): List<ProductOrderInfo> {
        val finalList = mutableListOf<ProductOrderInfo>()
        for( k in listOfProducts.indices ){
            val currentProduct = ProductOrderInfo(
                id = listOfProducts[k].id,
                contador = listOfProducts[k].contador,
                talla = listOfProducts[k].tallaSeleccionada,
                color = listOfProducts[k].colorSeleccionado
            )
            finalList.add(currentProduct)
        }

        return finalList
    }


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


    private fun disableClickAcceptSale(description: String, address: String){
        btnAcceptSale.isEnabled = !description.isNullOrEmpty() && !address.isNullOrEmpty()

        when(btnAcceptSale.isEnabled){
            true->{
                btnAcceptSale.setBackgroundColor( resources.getColor(R.color.blue_button) )
            }

            false->{
                btnAcceptSale.setBackgroundColor( resources.getColor(R.color.g_gray500) )
            }
        }


    }

    private fun validateUserDescriptionInput(){
        val description = etOrderDescription.text.toString()
        var disable = false

        if(description.length < MIN_LENGTH_DESCRIPTION){
            showToast("La descripción debe tener al menos $MIN_LENGTH_DESCRIPTION caracteres")
            disable = true
        }

        if(description.length > MAX_LENGTH_DESCRIPTION){
            showToast("La descripción debe tener máximo $MAX_LENGTH_DESCRIPTION caracteres")
            disable = true
        }

        btnAcceptSale.isEnabled = disable
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun clearCart(key: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}