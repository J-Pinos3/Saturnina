package com.example.saturninaapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
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

    private var itemsGeneralCommentaries = mutableListOf<ResultComment>()
    private lateinit var rvGeneralComments: RecyclerView
    private lateinit var generalCommentsAdapter: CommentsAdapter

    private lateinit var etGeneralInfoCommentary: EditText
    private lateinit var rbGeneralInfoRating: RatingBar
    private lateinit var btnSendGeneralComment: AppCompatButton

    private var CommentTextWatcher = object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val comment = etGeneralInfoCommentary.text.toString()

            disableClicCreateComment(comment)
            validateDescriptionLength(comment)
        }

    }


    private val MIN_DESCRIPTION_LENGTH = 10
    private val MAX_DESCRIPTION_LENGTH = 100

    private val TOTAL_ITEMS = 4

    private var random1: String = ""
    private var random2: String = ""
    private var sharedKey:String = "car_items"

    private var itemsProducts = mutableListOf<DetailProduct>()

    private lateinit var introDashboardItemsCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_dashboard_news)
        initUI()

        val user_token = intent.extras?.getString("USER_TOKEN")
        val user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")
        val bearerToken: String = "Bearer $user_token"

        loadItemsFromFile(sharedKey)
        loadCartItemsCount()

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


        CoroutineScope(Dispatchers.IO).launch {
            BringGeneralComments()
        }

        rvGeneralComments = findViewById(R.id.rvGeneralComments)
        generalCommentsAdapter = CommentsAdapter(itemsGeneralCommentaries)
        rvGeneralComments.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvGeneralComments.adapter = generalCommentsAdapter




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


        etGeneralInfoCommentary.addTextChangedListener(CommentTextWatcher)
        etGeneralInfoCommentary.setOnFocusChangeListener { view, b ->
            if(b)
                validateDescriptionLength(etGeneralInfoCommentary.text.toString())
        }

        btnSendGeneralComment.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                if (user_id != null) {
                    creareGeneralComment(bearerToken, etGeneralInfoCommentary.text.toString(),
                        user_id, rbGeneralInfoRating.rating.toInt())
                }
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


        //navigate
        val flCarritoComprasIcon: FrameLayout = findViewById(R.id.flCarritoCompras)
        flCarritoComprasIcon.setOnClickListener {
            Toast.makeText(this, "Carrito Clicado", Toast.LENGTH_SHORT).show()


            val intent = Intent(applicationContext, CarSalesActivity::class.java)
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
        secondCategoryItems = getProductsofRandomCat(secondCategoryItems, random2)
        Log.i("first cat", firstCategoryItems.toString() + "  categoria1 $random1")
        Log.i("second cat", secondCategoryItems.toString()  + "  categoria2 $random2")

        if( firstCategoryItems.size > TOTAL_ITEMS){
            firstCarouselAdapter.productSections = firstCategoryItems.take(TOTAL_ITEMS).toMutableList()
        }else{
            firstCarouselAdapter.productSections = firstCategoryItems
        }


        if(secondCategoryItems.size > TOTAL_ITEMS){
            secondCarouselAdapter.productSections = secondCategoryItems.take(TOTAL_ITEMS).toMutableList()
        }else{
            secondCarouselAdapter.productSections = secondCategoryItems
        }


        firstCarouselAdapter.notifyDataSetChanged()
        secondCarouselAdapter.notifyDataSetChanged()

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

        //cart items count
        introDashboardItemsCount = findViewById(R.id.action_cart_count)

        tvGotoFirstFilter = findViewById(R.id.tvGotoFirstFilter)
        tvGotoSecondFilter = findViewById(R.id.tvGotoSecondFilter)

        firstCarouselAdapter = ClothesCarouselAdapter(mutableListOf<DetailProduct>())
        secondCarouselAdapter = ClothesCarouselAdapter(mutableListOf<DetailProduct>())

        generalCommentsAdapter = CommentsAdapter(mutableListOf<ResultComment>())

        etGeneralInfoCommentary = findViewById(R.id.etGeneralInfoCommentary)
        rbGeneralInfoRating = findViewById(R.id.rbGeneralInfoRating)
        btnSendGeneralComment = findViewById(R.id.btnSendGeneralComment)
    }


    private fun disableClicCreateComment(comment: String) {
        btnSendGeneralComment.isClickable = !comment.isNullOrEmpty()

        when(btnSendGeneralComment.isClickable){
            true->{
                btnSendGeneralComment.setBackgroundColor( resources.getColor(R.color.blue_button) )
            }
            false->{
                btnSendGeneralComment.setBackgroundColor( resources.getColor(R.color.g_gray500) )
            }
        }
    }


    private fun validateDescriptionLength(comment: String){
        var clickable = true
        if(comment.length !in MIN_DESCRIPTION_LENGTH .. MAX_DESCRIPTION_LENGTH){
            etGeneralInfoCommentary.error = "El comentario debe tener una" +
                    " logitud entre $MIN_DESCRIPTION_LENGTH y $MAX_DESCRIPTION_LENGTH  caracteres"
            clickable = false
        }

        if(clickable){
            btnSendGeneralComment.setBackgroundColor( resources.getColor(R.color.blue_button) )
        }else{
            btnSendGeneralComment.setBackgroundColor( resources.getColor(R.color.g_gray500) )
        }

        btnSendGeneralComment.isClickable = clickable
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
        var categoryRandom = ""
        while( filteredList.isNullOrEmpty() ){
            categoryRandom = itemsCategories.random().id
            filteredList = listaProductos.filter { it.category == categoryRandom}
        }
        Log.i("FILTERED LIST-RANDOM CAT", "HERE $categoryRandom and $filteredList ")


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



    suspend fun creareGeneralComment(bearerToken: String, description: String, user_id: String, calificacion: Int){
        try{
            val commentaryData = CommentaryData(description, user_id,"",calificacion)
            val resultComment = ResultComment(calificacion,description,"","", UserId())

            val retrofitCreateGComment = RetrofitHelper.consumeAPI.createGenneralComment(bearerToken, commentaryData)

            if(retrofitCreateGComment.isSuccessful){
                val jsonResponse = retrofitCreateGComment.body()?.toString()
                val jsonObject = jsonResponse?.let { JSONObject(it) }
                val detailObject = jsonObject?.getJSONObject("detail")
                val msg = detailObject?.getString("msg")

                withContext(Dispatchers.Main){
                    insertCommentIntoList(resultComment)
                    Toast.makeText (this@IntroDashboardNews, msg, Toast.LENGTH_LONG).show()
                }
            }else{
                runOnUiThread {
                    val error = retrofitCreateGComment.errorBody()?.string()
                    val errorBody = error?.let { JSONObject(it) }
                    val detail = errorBody?.opt("detail")
                    var msg = ""
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
//                    Log.e("ERROR CREATING GEN COMMENT: ", "COULDN'T CREATE NEW COMMENT: ${retrofitCreateGComment.code()} \n\t--**-- $msg  --**--\n" +
//                            "\t $error -*-*-*- ${retrofitCreateGComment.errorBody().toString()}")
                }
            }

        }catch (e: Exception){
            Log.e("ERROR CONSUMING CREATE COMMENTS: ", e.message.toString())
        }
    }


    suspend fun BringGeneralComments(){
        try {
            val retrofitGetAllComments = RetrofitHelper.consumeAPI.getAllApplicationComments()
            if(retrofitGetAllComments.isSuccessful){
                val listResponse = retrofitGetAllComments.body()?.detail
                withContext(Dispatchers.Main){
                    if(listResponse != null){
                        for(k in listResponse){
                            for(comment in k.result){
                                itemsGeneralCommentaries.add(ResultComment(comment.calificacion, comment.descripcion,
                                        comment.id,"", comment.user_id))
                            }
                        }
                        Log.d("ALL COMMENTS", itemsGeneralCommentaries.toString())
                        generalCommentsAdapter.notifyDataSetChanged()
                    }

                }

            }else{
                Log.e("ERROR GETTING COMMENTS: ", "COULDN'T GET COMMENTS: ${retrofitGetAllComments.code()} --**-- ${retrofitGetAllComments.errorBody()?.string()}")
            }

        }catch (e: Exception){
            Log.e("ERROR CONSUMING BRING COMMENTS: ", e.message.toString())
        }
    }


    private fun insertCommentIntoList(commentaryData: ResultComment){
        itemsGeneralCommentaries.add(commentaryData)
        generalCommentsAdapter.commentsList = itemsGeneralCommentaries
        generalCommentsAdapter.notifyDataSetChanged()
    }


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

        println("carrito de productos: $itemsProducts")
    }

}
