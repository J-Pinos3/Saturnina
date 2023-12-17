package com.example.saturninaapp.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
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
import java.lang.Exception


class CompleteSaleActivity : AppCompatActivity() {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_complete_sale: NavigationView
    private lateinit var userOwner: DetailXUser

    lateinit var btnAcceptSale: AppCompatButton
    private var finalListOfProducts = mutableListOf<DetailProduct>()
    private lateinit var etOrderAddress: EditText


    private val pickImage = registerForActivityResult( ActivityResultContracts.StartActivityForResult() ){
        if(it.resultCode == Activity.RESULT_OK){
            val data = it.data
            val imgUri = data?.data

            CoroutineScope(Dispatchers.IO).launch {

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
        val bearerToken = "Bearer $userToken"
        if (totalItems != null) {
            showTotalCartItems(totalItems)
        }

        loadItemsFromFiles(cartKey)
        CoroutineScope(Dispatchers.IO).launch {
            userOwner = bringUserData(bearerToken)
        }


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
        apellido: String, direccion:String, telefono: String, descripcion: String, image: Uri,  productsData: List<ProductOrderInfo>
    ){
        try {
            //val filepart = MultipartBody.Part.createFormData("billIMG", image.toString())
            //val listPart = listOf( MultipartBody.Part.createFormData("products", productsData.toString()) )
            //val filepart = image.toMultipart("billImage")
            val file = image.path?.let { File(it) }
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val filepart = MultipartBody.Part.createFormData(
                "billImage",
                file?.name,
                requestFile
            )

            val listPart = mutableListOf<MultipartBody.Part>()
            for(product in productsData){
                val productPart = product.toMultipart("product")
                listPart.add(productPart)
            }

            val retrofitSendOrder = RetrofitHelper.consumeAPI.createOrder(
                bearerToken, userId, priceOrder, nombre, apellido, direccion, telefono, descripcion, filepart , listPart )


        }catch (e: Exception){
            e.printStackTrace()
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

    private fun ProductOrderInfo.toMultipart(partName: String): MultipartBody.Part{
        val json = Gson().toJson(this)
        val requestPart = RequestBody.create( MediaType.parse("multipart/form-data"), json )
        return MultipartBody.Part.createFormData( partName, json, requestPart )
    }
}