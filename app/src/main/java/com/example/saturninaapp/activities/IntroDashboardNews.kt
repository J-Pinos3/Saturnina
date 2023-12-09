package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.ClothesCarouselAdapter
import com.example.saturninaapp.adapters.ClothesCategoryAdapter
import com.example.saturninaapp.models.ClothCategoryData
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class IntroDashboardNews : AppCompatActivity() {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_main_news: NavigationView

    //clothes categories
    //private lateinit var clothesCategoryAdapter: ClothesCategoryAdapter
    private val itemsCategories = mutableListOf<ClothCategoryData>()
    //products
    private lateinit var rvFirstCategoryClothes: RecyclerView
    private lateinit var rvSecondCategoryClothes: RecyclerView
    private lateinit var firstCarouselAdapter: ClothesCarouselAdapter
    private lateinit var secondCarouselAdapter: ClothesCarouselAdapter
    private val firstCategoryItems = mutableListOf<DetailProduct>()
    private var secondCategoryItems = mutableListOf<DetailProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_dashboard_news)
        initUI()

        val user_token = intent.extras?.getString("USER_TOKEN")
        val bearerToken: String = "Bearer $user_token"


        CoroutineScope(Dispatchers.Main).launch {
            fetchIntroClothCategories(bearerToken)
            Log.d("DEBUG", "Categories fetched: $itemsCategories")
        }

        CoroutineScope(Dispatchers.IO).async{
            fecthIntroItemProducts(bearerToken)
            Log.d("DEBUG", "Products fetched: $firstCategoryItems")
        }

        //val firstFilteredList = getProductsofRandomCat(firstCategoryItems)
        //Log.i("firtst filter", firstFilteredList.toString())
        rvFirstCategoryClothes = findViewById(R.id.rvFirstCategoryClothes)//get products of a random category
        firstCarouselAdapter = ClothesCarouselAdapter(firstCategoryItems)
        rvFirstCategoryClothes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFirstCategoryClothes.adapter = firstCarouselAdapter


        //secondCategoryItems = getProductsofRandomCat(firstCategoryItems)
        //Log.i("second filter", secondCategoryItems.toString())
        rvSecondCategoryClothes = findViewById(R.id.rvSecondCategoryClothes)//get products of a random category
        secondCarouselAdapter = ClothesCarouselAdapter(secondCategoryItems)
        rvSecondCategoryClothes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSecondCategoryClothes.adapter = secondCarouselAdapter





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
                      //THIS WILL BE THE NEW MAIN ACTIVITY?????¡?
//                    val intent = Intent(this, DashboardActivity::class.java)
//                    startActivity(intent)

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

    }//ON CREATE



    private fun initUI() {
        //navigation
        drawer = findViewById(R.id.drawerLayoutMainNews)
        nav_view_main_news = findViewById(R.id.nav_view_main_news)

        firstCarouselAdapter = ClothesCarouselAdapter(mutableListOf<DetailProduct>())
        secondCarouselAdapter = ClothesCarouselAdapter(mutableListOf<DetailProduct>())
    }

    private fun getRandonCategory(): String{

        Log.i("CATEOGIRES SIZE", itemsCategories.size.toString())
        if(itemsCategories.size != 0 ){
            val randomCat = Random.nextInt(0, itemsCategories.size)
            return itemsCategories[randomCat].id
        }else{
            return "0"
        }

    }

    private fun getProductsofRandomCat(listaProductos: MutableList<DetailProduct> ): MutableList<DetailProduct> {

        val randomCategoryID: String = getRandonCategory()

        val filteredList =  listaProductos.filter { it.category == randomCategoryID }
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


    suspend fun fecthIntroItemProducts(bearerToken: String){
        try {
            val retrofitGetProducts = RetrofitHelper.consumeAPI.getItemsProducts(bearerToken)

            if(retrofitGetProducts.isSuccessful){
                val listResponse = retrofitGetProducts.body()?.detail
                //println(listResponse.toString() + "---size--- ${listResponse?.size}")
                withContext(Dispatchers.Main){
                    if(listResponse != null){
                        for(k in listResponse){
                            println("Cate: ${k.category}  Name: ${k.name}  Desc: ${k.descripcion}   IMG: ${k.imagen.secure_url}   Price: ${k.precio}")
                            firstCategoryItems.add(DetailProduct(k.category, k.descripcion, k.id, k.imagen, k.name, k.precio))
                            secondCategoryItems.add(DetailProduct(k.category, k.descripcion, k.id, k.imagen, k.name, k.precio))
                        }
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

}