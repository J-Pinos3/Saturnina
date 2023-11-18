package com.example.saturninaapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.ItemClothesAdapter
import com.example.saturninaapp.models.DetailProduct
import com.google.android.material.navigation.NavigationView

class MyProductsActivity : AppCompatActivity(){

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_my_products: NavigationView

    //for the recycler view that'll show items in the dashboard
    private lateinit var rvProductsManagement: RecyclerView
    private lateinit var itemClothesAdapter: ItemClothesAdapter

    private var itemsProducts = mutableListOf<DetailProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_products)
        initUI()

        //recycler
//        rvProductsManagement = findViewById(R.id.rvProductsManagement)
//        itemClothesAdapter = ItemClothesAdapter(itemsProducts)
//        rvProductsManagement.layoutManager = LinearLayoutManager(this)
//        rvProductsManagement.adapter = itemClothesAdapter


        //navigation

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav_view_my_products.setNavigationItemSelectedListener {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if( toggle.onOptionsItemSelected(item) ){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initUI(){
        //navigation
        drawer = findViewById(R.id.drawerLayoutMyProducts)
        nav_view_my_products = findViewById(R.id.nav_view_my_products)
    }





}//FIN DE LA CLASE MY PRODUCTS ACTIVITY