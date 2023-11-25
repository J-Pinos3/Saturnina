package com.example.saturninaapp.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.ItemClothesAdapter
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.util.UtilClasses
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class CarSalesActivity : AppCompatActivity(), UtilClasses  {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_car: NavigationView

    //for the recycler view that'll show items in the dashboard
    private lateinit var rvProductsCar: RecyclerView
    private lateinit var itemClothesAdapter: ItemClothesAdapter

    private var itemsProducts = mutableListOf<DetailProduct>()
    public var isAdapterVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_sales)
        val cartKey: String? = intent.extras?.getString("CARTKEY")
        initUI()
        if (cartKey != null) {
            loadItemsFromFile(cartKey)
        }

        //reycler
        rvProductsCar = findViewById(R.id.rvProductsCar)
        itemClothesAdapter = ItemClothesAdapter(itemsProducts,
            OnCLickListener = { item -> onItemClothSelected(item) },
            OnItemDeleteListener = {item ->  onItemDeleteSelected(item)},
            OnHideButton = { v,isVisible -> hideButtonDelete(v, isVisible) },
            isVisible = isAdapterVisible

        )
        rvProductsCar.layoutManager = LinearLayoutManager(this)
        rvProductsCar.adapter = itemClothesAdapter


        //navigation
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav_view_car.setNavigationItemSelectedListener {
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

    }// ON CREATE

    private fun hideButtonDelete(v: View, isVis: Boolean){
        if (isVis) v.visibility = View.GONE else v.visibility = View.VISIBLE
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if( toggle.onOptionsItemSelected(item) ){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initUI(){
        drawer = findViewById(R.id.drawerLayoutCar)
        nav_view_car = findViewById(R.id.nav_view_car)
    }


    private fun loadItemsFromFile(key: String){
        var sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
        val gson = Gson()

        val jsonString = sharedPreferences.getString(key,"")
        val type = object : TypeToken< MutableList<DetailProduct> >() {}.type
        itemsProducts = (gson.fromJson(jsonString, type) ?: emptyList<DetailProduct>()) as MutableList<DetailProduct>

    }


    private fun showCartListItems(){
        for(i in itemsProducts.indices){
            println("CARRITO ${itemsProducts[i].id} ${itemsProducts[i].name} ${itemsProducts[i].contador}")
        }
    }

    override fun onItemClothSelected(product: DetailProduct){
        Toast.makeText(this, product.id + " " + product.name + " " + product.precio, Toast.LENGTH_SHORT).show()
        //if the element is not in the list, add it
        if( !itemsProducts.contains(product) ){
            product.contador++
            itemsProducts.add(product)
            Log.d("Product Added To Cart","${product} added to cart")

        }else {
            //else just increase its counter
            var productIndex: Int = itemsProducts.indexOf(product)
            itemsProducts[productIndex].contador ++
            Log.d("Product Added To Cart"," ${product} ${product.contador}")
        }

        showCartListItems()
    }

    override fun onItemDeleteSelected(product: DetailProduct){
        Toast.makeText(this, "DELETE " + product.id + " " + product.name + " " + product.precio, Toast.LENGTH_SHORT).show()
        //if the element is still in the list, just reduce its counter
        //if there's no product, do nothing
        if( itemsProducts.contains(product) ){
            var productIndex: Int = itemsProducts.indexOf(product)

            if( itemsProducts[productIndex].contador >= 1 ){
                itemsProducts[productIndex].contador--

            }

            if(itemsProducts[productIndex].contador == 0){
                itemsProducts.removeAt(productIndex) //si el contador de ese producto es 0, lo eliminamos de la lista
            }

        }

        showCartListItems()
    }


}//FIN DE LA CLASE CAR SALES ACTIVITY