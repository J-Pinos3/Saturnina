package com.example.saturninaapp.activities

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
import androidx.drawerlayout.widget.DrawerLayout
import com.example.saturninaapp.R
import com.google.android.material.navigation.NavigationView

class CompleteSaleActivity : AppCompatActivity() {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_complete_sale: NavigationView

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
        cartSalesItemsCount.text = totalItems






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
                    Toast.makeText(this, "Item 1", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_item_two ->{
                    Toast.makeText(this, "Item 2", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_item_three ->{
                    Toast.makeText(this, "Item 3", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_item_four ->{
                    Toast.makeText(this, "Item 4", Toast.LENGTH_SHORT).show()
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


}