package com.example.saturninaapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.ClothesCategoryAdapter
import com.example.saturninaapp.adapters.ItemClothesAdapter
import com.example.saturninaapp.models.ClothCategoryData
import com.example.saturninaapp.models.Colore
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.models.Talla
import com.example.saturninaapp.util.RetrofitHelper
import com.example.saturninaapp.util.UtilClasses
import com.example.saturninaapp.viewholder.ItemClothesViewHolder
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

import java.lang.Exception

class DashboardActivity : AppCompatActivity(), UtilClasses {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view: NavigationView

    lateinit var bottom_nav_dashboard: BottomNavigationView

    //for the recycler view that'll show categories in the dashboard
    private lateinit var rvFilterClothes: RecyclerView
    private lateinit var clothesCategoryAdapter: ClothesCategoryAdapter
    //private var itemsCategories = mutableListOf<ClothCategoryData>()
    private val itemsCategories = mutableListOf<ClothCategoryData>()



    //for the recycler view that'll show items in the dashboard
    private lateinit var rvProductsDash: RecyclerView
    private lateinit var itemClothesAdapter: ItemClothesAdapter
    private var itemsProducts = mutableListOf<DetailProduct>()

    private var cartItems = mutableListOf<DetailProduct>()

    private var sharedKey:String = ""
    public var isAdapterVisible = true

    lateinit var nav_heaher_userrolDashboard: TextView
    private val ROL_USER: String = "rol:vuqn7k4vw0m1a3wt7fkb"

    private lateinit var cartSalesItemsCount: TextView

    override fun onPause() {
        super.onPause()
        val user_token = intent.extras?.getString("USER_TOKEN")
        val user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")
        sharedKey = user_id.toString()
        saveItemsToFile(sharedKey)
        //Toast.makeText(this@DashboardActivity,"SALIENDO DEL DASHBOARD", Toast.LENGTH_LONG).show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initUi()

        val user_token = intent.extras?.getString("USER_TOKEN")
        val user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")
        sharedKey = user_id.toString()
        val bearerToken: String = "Bearer $user_token"


        showCartListItems()
        loadItemsFromFile(sharedKey)
        loadInitialItemsCount()


        if(user_rol == ROL_USER){
            nav_heaher_userrolDashboard.text = "Cliente"
        }else{
            nav_heaher_userrolDashboard.text = "Administrador"
            val flCarritoCompras: FrameLayout = findViewById(R.id.flCarritoCompras)
            flCarritoCompras.visibility = View.GONE

        }


        val random_category_id = intent.extras?.getString("RANDOM_CATEGORY_ID")


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
            onHideItemCounter = {v, isVisible -> hideItemCartCounter(v, isVisible)},
            onChooseSize = {item, product -> onSizeSelected(item, product)},
            onChooseColor = {item, product -> onColorSelected(item, product)},
            user_rol.toString()
        )
        rvProductsDash.layoutManager = LinearLayoutManager(this)
        rvProductsDash.adapter = itemClothesAdapter


        if( intent.hasExtra("codIntro") ){
            if( !random_category_id.isNullOrEmpty() ){
                updateRandomItems(random_category_id)
                Log.i("RANDOM PRODUCT", "PRODUCTO RANDOM DE CATEGORÍA $random_category_id")
            }
            Log.i("RANDOM PRODUC1T", "PRODUCTO RANDOM1 DE CATEGORÍA $random_category_id")
        }

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

                    val intent = Intent(this, IntroDashboardNews::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.nav_item_three ->{

                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("USER_TOKEN_PROFILE", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)

                }


                R.id.nav_item_five ->{
                    //saveItemsToFile(sharedKey)
                    val intent = Intent(this, ManagementOptionsActivity::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
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


        //bottom navigation
        bottom_nav_dashboard.selectedItemId = R.id.bottom_nav_categories
        bottom_nav_dashboard.setOnNavigationItemSelectedListener {

            when(it.itemId){
                R.id.bottom_nav_home->{
                    //saveItemsToFile(sharedKey)
                    val intent = Intent(this, IntroDashboardNews::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.bottom_nav_categories->{ }

                R.id.bottom_nav_comments->{

                    val intent = Intent(this, GenneralComments::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }
            }

            true
        }



        val flCarritoCompras: FrameLayout = findViewById(R.id.flCarritoCompras)
        flCarritoCompras.setOnClickListener {
            //Toast.makeText(this, "Carrito Clicado", Toast.LENGTH_SHORT).show()
            //saveItemsToFile(sharedKey)

            val intent = Intent(this, CarSalesActivity::class.java)
            intent.putExtra("USER_TOKENTO_PROFILE", user_token)
            intent.putExtra("USER_ID", user_id)
            intent.putExtra("USER_ROL", user_rol)
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

    private fun updateRandomItems(idcategoria: String){
        val randomProducts: MutableList<DetailProduct> = itemsProducts.filter { it.category == idcategoria }.toMutableList()
        Log.i("RANDOM LIST OF PRODUCTS?", "$randomProducts")
        val randomCategoryChosen = itemsCategories.find { it.id == idcategoria }
        randomCategoryChosen?.isSelectedCategory = false

        if( !randomProducts.isNullOrEmpty() ){
            itemClothesAdapter.sellingItems = randomProducts
        }else{
            itemClothesAdapter.sellingItems = itemsProducts
        }
        itemClothesAdapter.notifyDataSetChanged()
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
        Toast.makeText(this, product.name + " Agregado al carrito" , Toast.LENGTH_SHORT).show()
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
        val gson: Gson = GsonBuilder()
            .create()
        //.registerTypeAdapterFactory(AlwaysListTypeAdapterFactory.create<Imagen>())
        val editor = sharedPreferences.edit()

        val jsonString = gson.toJson(cartItems)
        editor.putString(key, jsonString)
        editor.apply()

        //Toast.makeText(this, "Saved cartItems to sharedPreferences", Toast.LENGTH_SHORT).show()
    }


    private fun loadItemsFromFile(key: String){
        var sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
        val gson = Gson()

        val jsonString = sharedPreferences.getString(key,"")
        val type = object : TypeToken<MutableList<DetailProduct>>() {}.type
        cartItems = (gson.fromJson(jsonString, type) ?: mutableListOf<DetailProduct>() )

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

    //SIZES
    override fun onSizeSelected(spinner: AutoCompleteTextView, product: DetailProduct): Boolean{
        var listofSizes = getNameofSizes(product.tallas)
        var hasItems = true

        if( listofSizes.isNotEmpty() ){
            spinner.isEnabled = true
            if(  !product.tallaSeleccionada.isNullOrEmpty() ){
                var indiceTalla = listofSizes.find { it == product.tallaSeleccionada  }
                //Log.d("SIZES AND INDICES", "${listofSizes} --**-- ${indiceTalla}")
                spinner.setText(indiceTalla, false)

            }else{
                //var element = listofSizes.elementAt(0)
                spinner.setText( listofSizes.elementAt(0), false )
            }
        }else{
            hasItems = false
            spinner.setText( "N/A", false )
            spinner.isEnabled = false
        }

        return hasItems
    }

    private fun getNameofSizes(listSizes: List<Talla>?): ArrayList<String>{
        val listaNombres = arrayListOf<String>()
        if (listSizes != null) {
            for(k in listSizes){
                listaNombres.add(k.name)
            }
        }
        return listaNombres
    }

    //COLORS
    override fun onColorSelected(spinner: AutoCompleteTextView, product: DetailProduct): Boolean {
        var listofColors = getNameofColors(product.colores)
        var hasItems = true

        if(listofColors.isNotEmpty()){
            spinner.isEnabled = true
            if( !product.colorSeleccionado.isNullOrEmpty() ){
                //var indiceColor = listofColors.indexOf(product.colorSeleccionado)
                var indiceColor = listofColors.find { it == product.colorSeleccionado }
                spinner.setText(indiceColor, false)

            }else{

                spinner.setText(listofColors.elementAt(0), false)
            }
        }else{
            hasItems = false
            spinner.setText("N/A", false)
            spinner.isEnabled = false
        }

        return hasItems
    }


    private fun getNameofColors(listColors: List<Colore>?): ArrayList<String>{
        val listaColores = arrayListOf<String>()
        if (listColors != null) {
            for(k in listColors){
                listaColores.add(k.name)
            }
        }
        return listaColores
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


        //bottom navigation
        bottom_nav_dashboard = findViewById(R.id.bottom_nav_dashboard)

        //cart items count
        cartSalesItemsCount = findViewById(R.id.action_cart_count)

        nav_heaher_userrolDashboard = nav_view.getHeaderView(0).findViewById(R.id.nav_heaher_userrol)
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
                    val error = retrofitGetCategories.errorBody()?.string()
                    val errorBody = error?.let { JSONObject(it) }
                    val detail = errorBody?.opt("detail")
                    var msg =""

                    when(detail){
                        is JSONObject->{
                            msg = detail.getString("msg")
                        }

                        is JSONArray->{
                            val firstError = detail.getJSONObject(0)
                            msg = firstError.getString("msg")
                        }
                    }
                    Toast.makeText (this@DashboardActivity, msg, Toast.LENGTH_LONG).show()
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
                            println("Cate: ${k.category}  Name: ${k.name}  Desc: ${k.descripcion}  Price: ${k.precio}")

                            val colores = k.colores ?: emptyList()
                            val tallas = k.tallas ?: emptyList()

                            itemsProducts.add(DetailProduct(k.category,colores,k.descripcion, k.id, k.imagen, k.name, k.precio, tallas))

                        }
                        itemClothesAdapter.notifyDataSetChanged()
                    }
                }

            }else{
                runOnUiThread {
                    val error = retrofitGetProducts.errorBody()?.string()
                    val errorBody = error?.let { JSONObject(it) }
                    val detail = errorBody?.opt("detail")
                    var msg =""

                    when(detail){
                        is JSONObject ->{
                            msg = detail.getString("msg")
                        }

                        is JSONArray ->{
                            val firstError = detail.getJSONObject(0)
                            msg = firstError.getString("msg")
                        }
                    }
                    Toast.makeText (this@DashboardActivity, msg, Toast.LENGTH_LONG).show()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }//FIN DEL MÉTODO FETCH ITEM PRODUCTS



}