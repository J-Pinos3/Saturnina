package com.example.saturninaapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.ClothesCarouselAdapter
import com.example.saturninaapp.adapters.CommentsAdapter
import com.example.saturninaapp.models.ClothCategoryData
import com.example.saturninaapp.models.CommentaryData
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.models.ResultComment
import com.example.saturninaapp.models.UserId
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class IntroDashboardNews : AppCompatActivity() {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_main_news: NavigationView

    lateinit var bottom_nac_main_news: BottomNavigationView


    //clothes categories
    //private lateinit var clothesCategoryAdapter: ClothesCategoryAdapter
    private val itemsCategories = mutableListOf<ClothCategoryData>()
    //products
    private lateinit var rvFirstCategoryClothes: RecyclerView
    private lateinit var firstCarouselAdapter: ClothesCarouselAdapter
    private var firstCategoryItems = mutableListOf<DetailProduct>()



//

    private val TOTAL_ITEMS = 4

    private var random1: String = ""
    private var random2: String = ""
    private var sharedKey:String = ""

    private var itemsProducts = mutableListOf<DetailProduct>()

    private lateinit var introDashboardItemsCount: TextView

    lateinit var nav_heaher_userrolIntroNews: TextView
    private val ROL_USER: String = "rol:vuqn7k4vw0m1a3wt7fkb"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_dashboard_news)
        initUI()

        val user_token = intent.extras?.getString("USER_TOKEN")
        val user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")
        sharedKey=user_id.toString()
        val bearerToken: String = "Bearer $user_token"

        if(user_rol == ROL_USER){
            nav_heaher_userrolIntroNews.text = "Cliente"
        }else{
            nav_heaher_userrolIntroNews.text = "Administrador"
            val flCarritoCompras: FrameLayout = findViewById(R.id.flCarritoCompras)
            flCarritoCompras.visibility = View.GONE
        }

        loadItemsFromFile(sharedKey)
        loadCartItemsCount()

        CoroutineScope(Dispatchers.Main).launch {
            fetchIntroClothCategories(bearerToken)
            //Log.d("DEBUG", "Categories fetched: $itemsCategories")

            fecthIntroItemProducts(bearerToken) { ShuffleLists() }
          //  Log.d("DEBUG", "Products fetched: $firstCategoryItems")
        }



        //val firstFilteredList = getProductsofRandomCat(firstCategoryItems)
        //Log.i("firtst filter", firstFilteredList.toString())
        rvFirstCategoryClothes = findViewById(R.id.rvFirstCategoryClothes)//get products of a random category
        //firstCarouselAdapter = ClothesCarouselAdapter(firstCategoryItems)
        rvFirstCategoryClothes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFirstCategoryClothes.adapter = firstCarouselAdapter





        //navigation
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nav_view_main_news.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_item_one ->{
                      //THIS the INTRO DASHBOARD NEWS
                        //it.isVisible = false
                }

                R.id.nav_item_three ->{
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("USER_TOKEN_PROFILE", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }


                R.id.nav_item_five ->{
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
        bottom_nac_main_news.selectedItemId = R.id.bottom_nav_home
        bottom_nac_main_news.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_nav_home->{  }

                R.id.bottom_nav_categories->{
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

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


        //navigate
        val flCarritoComprasIcon: FrameLayout = findViewById(R.id.flCarritoCompras)
        flCarritoComprasIcon.setOnClickListener {
            //Toast.makeText(this, "Carrito Clicado", Toast.LENGTH_SHORT).show()


            val intent = Intent(this, CarSalesActivity::class.java)
            intent.putExtra("USER_TOKENTO_PROFILE", user_token)
            intent.putExtra("USER_ID", user_id)
            intent.putExtra("USER_ROL", user_rol)
            startActivity(intent)
        }

    }//ON CREATE


    fun ShuffleLists() {

        random1 = getRandonCategory()
        random2 = getRandonCategory()
        firstCategoryItems = getProductsofRandomCat(firstCategoryItems, random1)
        //Log.i("first cat", firstCategoryItems.toString() + "  categoria1 $random1")

        if( firstCategoryItems.size > TOTAL_ITEMS){
            firstCarouselAdapter.productSections = firstCategoryItems.take(TOTAL_ITEMS).toMutableList()
        }else{
            firstCarouselAdapter.productSections = firstCategoryItems
        }

        firstCarouselAdapter.notifyDataSetChanged()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if( toggle.onOptionsItemSelected(item) ){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initUI() {
        //navigation
        drawer = findViewById(R.id.drawerLayoutMainNews)
        nav_view_main_news = findViewById(R.id.nav_view_main_news)

        //bottom navigation
        bottom_nac_main_news= findViewById(R.id.bottom_nac_main_news)

        //cart items count
        introDashboardItemsCount = findViewById(R.id.action_cart_count)


        firstCarouselAdapter = ClothesCarouselAdapter(mutableListOf<DetailProduct>())

        //nav_heaher_userrolIntroNews = findViewById(R.id.nav_heaher_userrol)
        nav_heaher_userrolIntroNews = nav_view_main_news.getHeaderView(0).findViewById(R.id.nav_heaher_userrol)
    }



    private fun loadCartItemsCount(){
        var suma: Int = 0
        for(k in itemsProducts){
            suma += k.contador
        }

        introDashboardItemsCount.text = suma.toString()
    }


    private fun navigateToDashboard(token: String, categoryID: String, userID: String, userRol: String){
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("USER_TOKEN", token)
        intent.putExtra("RANDOM_CATEGORY_ID", categoryID)
        intent.putExtra("USER_ID", userID)
        intent.putExtra("USER_ROL", userRol)
        intent.putExtra("codIntro", 11)
        startActivity(intent)
    }

    private fun getRandonCategory(): String{

        //Log.i("CATEGORIES SIZE", itemsCategories.size.toString())
        return if(itemsCategories.size != 0 ){
            val randomcar = itemsCategories.random()
            randomcar.id
            /*
                val indice = itemsCategories.indexOf(randomcar)
                return itemsCategories[indice].id
                val randomCat = Random.nextInt(0, itemsCategories.size)
                return itemsCategories[randomCat].id
                 */
        }else{
            "0"
        }

    }


    private fun getProductsofRandomCat(listaProductos: MutableList<DetailProduct>,randomCategoryID: String ): MutableList<DetailProduct> {

        var filteredList =  listaProductos.filter { it.category == randomCategoryID }
        var categoryRandom = ""
        while( filteredList.isNullOrEmpty() ){
            categoryRandom = itemsCategories.random().id
            filteredList = listaProductos.filter { it.category == categoryRandom}
        }
        //Log.i("FILTERED LIST-RANDOM CAT", "HERE $categoryRandom and $filteredList ")


        return filteredList.toMutableList()
    }



    suspend fun fetchIntroClothCategories(bearerToken: String){
        //val categoriesList = mutableListOf<ClothCategoryData>()

        try {
            val retrofitGetCategories = RetrofitHelper.consumeAPI.getClothesCategories(bearerToken)
            if(retrofitGetCategories.isSuccessful){
                val listResponse = retrofitGetCategories.body()?.detail
                println(listResponse.toString() + "****-**-  ${listResponse?.size}")
                withContext(Dispatchers.Main){
                    if (listResponse != null) {
                        for(k in listResponse){
                            //println("ID: " + k.id + " CATE: " + k.name)
                            itemsCategories.add(ClothCategoryData(k.id, k.name))
                        }

                        //clothesCategoryAdapter.notifyDataSetChanged()
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
                    Toast.makeText (this@IntroDashboardNews, msg, Toast.LENGTH_LONG).show()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

        //return categoriesList
    }//FIN DEL MÉTODO FETCH INTRO CLOTH CATEGORIES


    suspend fun fecthIntroItemProducts(bearerToken: String, onProductsFetched: () -> Unit){
        try {
            val retrofitGetProducts = RetrofitHelper.consumeAPI.getItemsProducts(bearerToken)

            if(retrofitGetProducts.isSuccessful){
                val listResponse = retrofitGetProducts.body()?.detail
                //println(listResponse.toString() + "---size--- ${listResponse?.size}")
                withContext(Dispatchers.Main){
                    if(listResponse != null){
                        for(k in listResponse){
                            //println("Cate: ${k.category}  Name: ${k.name}  Desc: ${k.descripcion}  Price: ${k.precio}")

                            val colores = k.colores ?: emptyList()
                            val tallas = k.tallas ?: emptyList()

                            firstCategoryItems.add(DetailProduct(k.category,colores, k.descripcion, k.id, k.imagen, k.name, k.precio, tallas))

                            //k.imagen?.get(0)?.secure_url
                        }
                        onProductsFetched()

                        firstCarouselAdapter.notifyDataSetChanged()

                    }
                }

            }else{
                runOnUiThread {
                    val error = retrofitGetProducts.errorBody()?.string()
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
                    Toast.makeText (this@IntroDashboardNews, msg, Toast.LENGTH_LONG).show()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }//FIN DEL MÉTODO FETCH INTRO ITEM PRODUCTS


//    private fun clearCart(key: String){
//        val sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
//        sharedPreferences.edit().clear().apply()
//    }

    private fun loadItemsFromFile(Key: String){
        var sharedPreferences: SharedPreferences = getSharedPreferences(Key, MODE_PRIVATE)
        val gson = Gson()

        val jsonString = sharedPreferences.getString(Key,"")
        val type = object : TypeToken<MutableList<DetailProduct>>() {}.type
        itemsProducts = (gson.fromJson(jsonString, type) ?: mutableListOf<DetailProduct>() )

        //println("carrito de productos: $itemsProducts")
    }

}
