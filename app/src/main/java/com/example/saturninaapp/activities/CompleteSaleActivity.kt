package com.example.saturninaapp.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
import androidx.drawerlayout.widget.DrawerLayout
import com.example.saturninaapp.R
import com.example.saturninaapp.models.DetailXUser
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class CompleteSaleActivity : AppCompatActivity() {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_complete_sale: NavigationView
    private lateinit var userOwner: DetailXUser

    lateinit var btnAcceptSale: AppCompatButton


    private val pickImage = registerForActivityResult( ActivityResultContracts.GetContent() ){
        uri: Uri? ->
        if( uri != null){
            println("IMAGEN SELECCIONADA: ${uri}")
        }
    }

    private lateinit var cartSalesItemsCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_sale)
        initUI()
        val totalItems: String? = intent.extras?.getString("TOTAL_CART_ITEMS")
        val userToken = intent.extras?.getString("USER_TOKENTO_PROFILE")
        val bearerToken: String = "Bearer $userToken"
        cartSalesItemsCount.text = totalItems

        CoroutineScope(Dispatchers.IO).launch {
            userOwner = bringUserData(bearerToken)
        }






        btnAcceptSale.setOnClickListener {
            pickImage.launch("image/*")
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

                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    //Toast.makeText(this, "Item 1", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_item_two ->{

                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("USER_TOKEN_PROFILE", userToken)
                    startActivity(intent)
                    //Toast.makeText(this, "Item 2", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_item_three ->{
                    //Toast.makeText(this, "Item 3", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_item_four ->{
                    val intent = Intent(this, ManagementOptionsActivity::class.java)
                    startActivity(intent)
                    //Toast.makeText(this, "Item 4", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_item_five ->{
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    //Toast.makeText(this, "Item 4", Toast.LENGTH_SHORT).show()
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

        //send order button / accept sale
        btnAcceptSale = findViewById(R.id.btnAcceptSale)
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



}