package com.example.saturninaapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.ClothesCategoryAdapter
import com.example.saturninaapp.adapters.ItemClothesAdapter
import com.example.saturninaapp.models.ClothCategoryData
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.util.RetrofitHelper
import com.example.saturninaapp.util.UtilClasses
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.io.FileOutputStream

import java.lang.Exception

class DashboardActivity : AppCompatActivity(), UtilClasses {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view: NavigationView

    //for the recycler view that'll show categories in the dashboard
    private lateinit var rvFilterClothes: RecyclerView
    private lateinit var clothesCategoryAdapter: ClothesCategoryAdapter
    //private var itemsCategories = mutableListOf<ClothCategoryData>()
    private val itemsCategories = mutableListOf<ClothCategoryData>(ClothCategoryData("1","Todo"))



    //for the recycler view that'll show items in the dashboard
    private lateinit var rvProductsDash: RecyclerView
    private lateinit var itemClothesAdapter: ItemClothesAdapter
    private var itemsProducts = mutableListOf<DetailProduct>()

    private var cartItems = mutableListOf<DetailProduct>()

    private var sharedKey:String = "car_items"
    public var isAdapterVisible = true

    private lateinit var cartSalesItemsCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initUi()
        showCartListItems()
        loadItemsFromFile(sharedKey)
        loadInitialItemsCount()



        val user_token = intent.extras?.getString("USER_TOKEN")
        val bearerToken: String = "Bearer $user_token"

        //get categories from API
        CoroutineScope(Dispatchers.IO).launch {
            fetchClothCategories(bearerToken)
        }

        //get products from API
        CoroutineScope(Dispatchers.IO).launch {
            fecthItemProducts(bearerToken)
        }

        //recycler to show categories
        rvFilterClothes = findViewById(R.id.rvFilterClothes)
        clothesCategoryAdapter = ClothesCategoryAdapter(itemsCategories){ position -> updateCateories(position) }
        rvFilterClothes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFilterClothes.adapter = clothesCategoryAdapter



        //recycler to show products
        rvProductsDash = findViewById(R.id.rvProductsDash)
        //itemClothesAdapter = ItemClothesAdapter(itemsProducts,onItemDeleteSelected()){ onItemClothSelected(it) }
        itemClothesAdapter = ItemClothesAdapter(itemsProducts,
            OnCLickListener = { item -> onItemClothSelected(item) },
            OnItemDeleteListener = {item ->  onItemDeleteSelected(item)},
            OnHideButton = { v,isVisible -> hideButtonDelete(v, isVisible) },
            isVisible = isAdapterVisible,
            onHideItemCounter = {v, isVisible -> hideItemCartCounter(v, isVisible)}
        )
        rvProductsDash.layoutManager = LinearLayoutManager(this)
        rvProductsDash.adapter = itemClothesAdapter


        //navigation

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav_view.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_item_one ->{
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)

                    //Toast.makeText(this, "Item 1", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_item_two ->{
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("USER_TOKEN_PROFILE", user_token)
                    startActivity(intent)

                    //Toast.makeText(this, "Item 2", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_item_three ->{
                    //val intent = Intent(this, AboutActivity::class.java)
                    //startActivity(intent)
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
                }
            }

            true
        }


        val flCarritoCompras: FrameLayout = findViewById(R.id.flCarritoCompras)
        flCarritoCompras.setOnClickListener {
            Toast.makeText(this, "Carrito Clicado", Toast.LENGTH_SHORT).show()

            saveItemsToFile(sharedKey)

            val intent = Intent(applicationContext, CarSalesActivity::class.java)
            intent.putExtra("CARTKEY", sharedKey)
            intent.putExtra("USER_TOKENTO_PROFILE", user_token)
            startActivity(intent)
        }
    }//ON CREATE

    private fun hideButtonDelete(v: View, isVis: Boolean){
        if (isVis) v.visibility = View.GONE else v.visibility = View.VISIBLE
    }

    private fun hideItemCartCounter(v: View, isVis: Boolean){
        if (isVis) v.visibility = View.GONE else v.visibility = View.VISIBLE
    }

    private fun updateCateories(position: Int){
        itemsCategories[position].isSelectedCategory = !itemsCategories[position].isSelectedCategory
        clothesCategoryAdapter.notifyItemChanged(position)  //.notifyDataSetChanged()

        updateItemsClothes()
    }

    private fun updateItemsClothes(){
        //obtengo las categorías seleccionadas y según eso muestro la ropa
        val selectedCategories: List<ClothCategoryData> = itemsCategories.filter { it.isSelectedCategory == false }
        var newClothes =  mutableListOf<DetailProduct>()
        println("categorías seleccionadas ${selectedCategories}")
        for(k in itemsProducts){
            if( selectedCategories.any {  it.id == k.category } )
            {

                newClothes.add(k)
            }

        }
        if( !selectedCategories.isNullOrEmpty() ){
            itemClothesAdapter.sellingItems = newClothes
        }else{
            itemClothesAdapter.sellingItems = itemsProducts
        }


        itemClothesAdapter.notifyDataSetChanged()
    }


    //this function will handle add item to cart
    override fun onItemClothSelected(product: DetailProduct){
        Toast.makeText(this, product.id + " " + product.name + " " + product.precio, Toast.LENGTH_SHORT).show()
        //if the element is not in the list, add it
        val existingProduct = cartItems.find { it.id == product.id }

        /*
        if( !cartItems.contains(product) ){
            product.contador++
            cartItems.add(product)
            increaseCartItemsCount()
            Log.d("Dash: Product Added To Cart","${product} ")

        }else {
            //else just increase its counter
            var productIndex: Int = cartItems.indexOf(product)
            cartItems[productIndex].contador ++
            increaseCartItemsCount()
            Log.d("Dash: Product counter increased"," ${product} ${product.contador}")
        }
        */

        if( existingProduct == null){
            product.contador++
            cartItems.add(product)
            increaseCartItemsCount()
            Log.d("Dash: Product Added To Cart","${product} ")

        }else {
            //else just increase its counter
            existingProduct.contador++
            increaseCartItemsCount()
            Log.d("Dash: Product counter increased"," ${existingProduct} ${existingProduct.contador}")
        }
        showCartListItems()
    }

    private fun showCartListItems(){
        for(i in cartItems.indices){
            println("CARRITO ${cartItems[i].id} ${cartItems[i].name} ${cartItems[i].contador}")
        }
    }


    private fun saveItemsToFile(key: String){
        var sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
        val gson: Gson = Gson()
        val editor = sharedPreferences.edit()

        val jsonString = gson.toJson(cartItems)
        editor.putString(key, jsonString)
        editor.apply()

        Toast.makeText(this, "Saved cartItems to sharedPreferences", Toast.LENGTH_SHORT).show()
    }


    private fun loadItemsFromFile(key: String){
        var sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
        val gson = Gson()

        val jsonString = sharedPreferences.getString(key,"")
        val type = object : TypeToken<MutableList<DetailProduct>>() {}.type
        cartItems = (gson.fromJson(jsonString, type) ?: emptyList<DetailProduct>()) as MutableList<DetailProduct>
    }

    private fun loadInitialItemsCount(){
        var suma: Int = 0
        for(k in cartItems){
            suma += k.contador
        }

        cartSalesItemsCount.text = suma.toString()
    }

    private fun increaseCartItemsCount(){
        cartSalesItemsCount.text = (cartSalesItemsCount.text.toString().toInt() + 1).toString()
    }

    //this function will handle delete Item from cart
    override fun onItemDeleteSelected(product: DetailProduct){
//        Toast.makeText(this, "DELETE " + product.id + " " + product.name + " " + product.precio, Toast.LENGTH_SHORT).show()
//        //if the element is still in the list, just reduce its counter
//        //if there's no product, do nothing
//        if( cartItems.contains(product) ){
//            var productIndex: Int = cartItems.indexOf(product)
//
//            if( cartItems[productIndex].contador >= 1 ){
//                cartItems[productIndex].contador--
//
//            }
//
//            if(cartItems[productIndex].contador == 0){
//                cartItems.removeAt(productIndex) //si el contador de ese producto es 0, lo eliminamos de la lista
//            }
//
//        }
//
//        showCartListItems()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if( toggle.onOptionsItemSelected(item) ){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initUi(){
        //navigation
        drawer = findViewById(R.id.drawerLayout)
        nav_view = findViewById(R.id.nav_view)

        //cart items count
        cartSalesItemsCount = findViewById(R.id.action_cart_count)
    }


    //suspend fun fetchClothCategories(bearerToken: String): List<ClothCategoryData>{
    suspend fun fetchClothCategories(bearerToken: String){
        //val categoriesList = mutableListOf<ClothCategoryData>()

        try {
            val retrofitGetCategories = RetrofitHelper.consumeAPI.getClothesCategories(bearerToken)
            if(retrofitGetCategories.isSuccessful){
                val listResponse = retrofitGetCategories.body()?.detail
                //println(listResponse.toString() + "  ${listResponse?.size}")
                withContext(Dispatchers.Main){
                    if (listResponse != null) {
                        for(k in listResponse){
                            println("ID: " + k.id + " CATE: " + k.name)
                            itemsCategories.add(ClothCategoryData(k.id, k.name))
                        }

                        clothesCategoryAdapter.notifyDataSetChanged()
                    }

                }

            }else{
                runOnUiThread {
                    Log.e("CANT BRING CATS", "${retrofitGetCategories.code()} --- ${retrofitGetCategories.errorBody()?.toString()}")
                }
            }
        }catch (e: Exception){
                e.printStackTrace()
        }

        //return categoriesList
    }//FIN DEL MÉTODO FETCH CLOTH CATEGORIES


    suspend fun fecthItemProducts(bearerToken: String){
        try {
            val retrofitGetProducts = RetrofitHelper.consumeAPI.getItemsProducts(bearerToken)

            if(retrofitGetProducts.isSuccessful){
                val listResponse = retrofitGetProducts.body()?.detail
                //println(listResponse.toString() + "---size--- ${listResponse?.size}")
                withContext(Dispatchers.Main){
                    if(listResponse != null){
                        for(k in listResponse){
                            println("Cate: ${k.category}  Name: ${k.name}  Desc: ${k.descripcion}   IMG: ${k.imagen.secure_url}   Price: ${k.precio}")
                            itemsProducts.add(DetailProduct(k.category, k.descripcion, k.id, k.imagen, k.name, k.precio))
                        }
                        itemClothesAdapter.notifyDataSetChanged()
                    }
                }

            }else{
                runOnUiThread {
                    Log.e("CANT BRING CATS", "${retrofitGetProducts.code()} --- ${retrofitGetProducts.errorBody()?.toString()}")
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }//FIN DEL MÉTODO FETCH ITEM PRODUCTS



}