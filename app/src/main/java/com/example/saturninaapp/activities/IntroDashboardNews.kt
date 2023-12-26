package com.example.saturninaapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.ClothesCarouselAdapter
import com.example.saturninaapp.models.ClothCategoryData
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.models.Imagen
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class IntroDashboardNews : AppCompatActivity() {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_main_news: NavigationView

    private lateinit var tvGotoFirstFilter: TextView
    private lateinit var tvGotoSecondFilter: TextView

    //clothes categories
    //private lateinit var clothesCategoryAdapter: ClothesCategoryAdapter
    private val itemsCategories = mutableListOf<ClothCategoryData>()
    //products
    private lateinit var rvFirstCategoryClothes: RecyclerView
    private lateinit var rvSecondCategoryClothes: RecyclerView
    private lateinit var firstCarouselAdapter: ClothesCarouselAdapter
    private lateinit var secondCarouselAdapter: ClothesCarouselAdapter
    private var firstCategoryItems = mutableListOf<DetailProduct>()
    private var secondCategoryItems = mutableListOf<DetailProduct>()

    private var random1: String = ""
    private var random2: String = ""
    private var sharedKey:String = "car_items"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_dashboard_news)
        initUI()

        val user_token = intent.extras?.getString("USER_TOKEN")
        val user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")
        val bearerToken: String = "Bearer $user_token"


        CoroutineScope(Dispatchers.Main).launch {
            fetchIntroClothCategories(bearerToken)
            Log.d("DEBUG", "Categories fetched: $itemsCategories")

            fecthIntroItemProducts(bearerToken) { ShuffleLists() }
            Log.d("DEBUG", "Products fetched: $firstCategoryItems")
        }



        //val firstFilteredList = getProductsofRandomCat(firstCategoryItems)
        //Log.i("firtst filter", firstFilteredList.toString())
        rvFirstCategoryClothes = findViewById(R.id.rvFirstCategoryClothes)//get products of a random category
        //firstCarouselAdapter = ClothesCarouselAdapter(firstCategoryItems)
        rvFirstCategoryClothes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFirstCategoryClothes.adapter = firstCarouselAdapter


        //secondCategoryItems = getProductsofRandomCat(firstCategoryItems)
        //Log.i("second filter", secondCategoryItems.toString())
        rvSecondCategoryClothes = findViewById(R.id.rvSecondCategoryClothes)//get products of a random category
        //secondCarouselAdapter = ClothesCarouselAdapter(secondCategoryItems)
        rvSecondCategoryClothes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSecondCategoryClothes.adapter = secondCarouselAdapter


        tvGotoFirstFilter.setOnClickListener {
            if (user_token != null && user_id != null && user_rol != null) {

                    navigateToDashboard(user_token,random1, user_id, user_rol)
            }
        }

        tvGotoSecondFilter.setOnClickListener {
            if (user_token != null && user_id != null && user_rol != null) {

                    navigateToDashboard(user_token, random2, user_id, user_rol)
            }
        }


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
                R.id.nav_item_two ->{
                    val intent = Intent(this, DashboardActivity::class.java)
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
                R.id.nav_item_four ->{
                    //NOSOTROS
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

    }//ON CREATE


    fun ShuffleLists() {

        random1 = getRandonCategory()
        random2 = getRandonCategory()
        firstCategoryItems = getProductsofRandomCat(firstCategoryItems, random1)
        secondCategoryItems = getProductsofRandomCat(secondCategoryItems, random2)
        Log.i("first cat", firstCategoryItems.toString() + "  categoria1 $random1")
        Log.i("second cat", secondCategoryItems.toString()  + "  categoria2 $random2")

        firstCarouselAdapter.productSections = firstCategoryItems
        secondCarouselAdapter.productSections = secondCategoryItems

        firstCarouselAdapter.notifyDataSetChanged()
        secondCarouselAdapter.notifyDataSetChanged()

    }


    private fun initUI() {
        //navigation
        drawer = findViewById(R.id.drawerLayoutMainNews)
        nav_view_main_news = findViewById(R.id.nav_view_main_news)

        tvGotoFirstFilter = findViewById(R.id.tvGotoFirstFilter)
        tvGotoSecondFilter = findViewById(R.id.tvGotoSecondFilter)

        firstCarouselAdapter = ClothesCarouselAdapter(mutableListOf<DetailProduct>())
        secondCarouselAdapter = ClothesCarouselAdapter(mutableListOf<DetailProduct>())
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

        Log.i("CATEGORIES SIZE", itemsCategories.size.toString())
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
        Log.i("FILTERED LIST WITH RANDOM CAT", "HERE $randomCategoryID and $filteredList ")


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
                            println("ID: " + k.id + " CATE: " + k.name)
                            itemsCategories.add(ClothCategoryData(k.id, k.name))
                        }

                        //clothesCategoryAdapter.notifyDataSetChanged()
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
                            println("Cate: ${k.category}  Name: ${k.name}  Desc: ${k.descripcion}  Price: ${k.precio}")

                            val colores = k.colores ?: emptyList()
                            val tallas = k.tallas ?: emptyList()

                            firstCategoryItems.add(DetailProduct(k.category,colores, k.descripcion, k.id, k.imagen, k.name, k.precio, tallas))
                            secondCategoryItems.add(DetailProduct(k.category,colores, k.descripcion, k.id, k.imagen, k.name, k.precio, tallas))

                            //k.imagen?.get(0)?.secure_url
                        }
                        onProductsFetched()

                        firstCarouselAdapter.notifyDataSetChanged()
                        secondCarouselAdapter.notifyDataSetChanged()
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
    }//FIN DEL MÉTODO FETCH INTRO ITEM PRODUCTS


    private fun clearCart(key: String){
        val sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

}

//                            if(imagen is Imagen ){
//                                print("imagen111 ${imagen}")
//                                firstCategoryItems.add(DetailProduct(k.category,colores, k.descripcion, k.id, listOf(imagen.secure_url), k.name, k.precio, tallas))
//                                secondCategoryItems.add(DetailProduct(k.category,colores, k.descripcion, k.id, listOf(imagen.secure_url), k.name, k.precio, tallas))
//
//                            }else if(imagen is List<*>){
//                                val images = imagen.filterIsInstance<Imagen>()
//                                firstCategoryItems.add(DetailProduct(k.category,colores, k.descripcion, k.id, images, k.name, k.precio, tallas))
//                                secondCategoryItems.add(DetailProduct(k.category,colores, k.descripcion, k.id, images, k.name, k.precio, tallas))
//                                print("imagenes222 ${images}")
//                            }else{
//                                firstCategoryItems.add(DetailProduct(k.category,colores,k.descripcion, k.id, imagen, k.name, k.precio, tallas))
//                                secondCategoryItems.add(DetailProduct(k.category,colores,k.descripcion, k.id, imagen, k.name, k.precio, tallas))
//                                print("imagen333 ${imagen}")
//                            }