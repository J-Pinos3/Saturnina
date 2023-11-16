package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.ClothesCategoryAdapter
import com.example.saturninaapp.adapters.ItemClothesAdapter
//import com.example.saturninaapp.adapters.OnItemCategoryListener
import com.example.saturninaapp.models.ClothCategoryData
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class DashboardActivity : AppCompatActivity() {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view: NavigationView

    //for the recycler view that'll show categories in the dashboard
    private lateinit var rvFilterClothes: RecyclerView
    private lateinit var clothesCategoryAdapter: ClothesCategoryAdapter
    //private var itemsCategories = mutableListOf<ClothCategoryData>()
    private val itemsCategories = mutableListOf<ClothCategoryData>(
        ClothCategoryData("1","zapatos"),
        ClothCategoryData("2","pinturas"),
        ClothCategoryData("3","camisetas"),
        ClothCategoryData("4","jeans"),
        ClothCategoryData("5","hoodie")
    )



    //for the recycler view that'll show items in the dashboard
    private lateinit var rvProductsDash: RecyclerView
    private lateinit var itemClothesAdapter: ItemClothesAdapter
    private var itemsProducts = mutableListOf<DetailProduct>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initUi()
        val user_token = intent.extras?.getString("USER_TOKEN")
        val bearerToken: String = "Bearer "+user_token

        //try to categories from API
        CoroutineScope(Dispatchers.IO).launch {
            fetchClothCategories(bearerToken)
        }

        CoroutineScope(Dispatchers.IO).launch {
            fecthItemProducts(bearerToken)
        }

        //recycler to show categories
        rvFilterClothes = findViewById(R.id.rvFilterClothes)
        clothesCategoryAdapter = ClothesCategoryAdapter(itemsCategories){ position -> updateCateories(position) }
                //Toast.makeText(applicationContext, "${itemsCategories[position].name} was chosen", Toast.LENGTH_SHORT).show()

        rvFilterClothes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFilterClothes.adapter = clothesCategoryAdapter



        //recycler to show products
        rvProductsDash = findViewById(R.id.rvProductsDash)
        itemClothesAdapter = ItemClothesAdapter(itemsProducts)
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
                    Toast.makeText(this, "Item 3", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_item_four ->{
                    Toast.makeText(this, "Item 4", Toast.LENGTH_SHORT).show()
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

    private fun updateCateories(position: Int){
        itemsCategories[position].isSelectedCategory = !itemsCategories[position].isSelectedCategory
        clothesCategoryAdapter.notifyItemChanged(position)  //.notifyDataSetChanged()
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
    }


    //suspend fun fetchClothCategories(bearerToken: String): List<ClothCategoryData>{
    suspend fun fetchClothCategories(bearerToken: String){
        //val categoriesList = mutableListOf<ClothCategoryData>()

        try {
            val retrofitGetCategories = RetrofitHelper.consumeAPI.getClothesCategories(bearerToken)
            if(retrofitGetCategories.isSuccessful){
                val listResponse = retrofitGetCategories.body()?.detail
                println(listResponse.toString() + "  ${listResponse?.size}")
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
                println(listResponse.toString() + "---size--- ${listResponse?.size}")
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