package com.example.saturninaapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.CommentsAdapter
import com.example.saturninaapp.models.Colore
import com.example.saturninaapp.models.CommentaryData
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.models.ResultComment
import com.example.saturninaapp.models.Talla
import com.example.saturninaapp.models.UserId
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import org.imaginativeworld.whynotimagecarousel.ImageCarousel

class ShowProductInfo : AppCompatActivity() {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_produt_info: NavigationView

    private val imagesList = mutableListOf<CarouselItem>()
    private lateinit var icCarousel: ImageCarousel

    private lateinit var tvProductInfoName: TextView
    private lateinit var tvProductInfoDescription: TextView
    private lateinit var tvProductInfoPrice: TextView

    private lateinit var etProductInfoCommentary: EditText

    private lateinit var rbProductInfoRating: RatingBar

    private lateinit var btnSendCommentary: AppCompatButton

    private lateinit var rvComments: RecyclerView
    private lateinit var commentsAdapter: CommentsAdapter
    private var itemsCommentaries = mutableListOf<ResultComment>()
    private var filteredCommentaries = mutableListOf<ResultComment>()

    private lateinit var spProductInfoSizesChoice: AutoCompleteTextView
    private lateinit var spProductInfoColorsChoice: AutoCompleteTextView

    private var fileKey: String = "user_data"
    private var user_token: String = ""
    private var user_id: String = ""
    private var user_rol: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_product_info)
        initUI()

        loadIdTokenRoleFromFile(fileKey)
        val bearerToken = "Bearer $user_token"
        val productData = intent.getSerializableExtra("PRODUCT_DATA") as DetailProduct
        CoroutineScope(Dispatchers.IO).launch {
            bringAllComments(bearerToken, { itemsCommentaries, productData ->  filterCommentsOfProduct(itemsCommentaries, productData) }, productData )
        }


        loadScreenItemsWithProductData(productData, spProductInfoColorsChoice, spProductInfoSizesChoice)



        commentsAdapter = CommentsAdapter(itemsCommentaries)
        rvComments.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvComments.adapter = commentsAdapter


        btnSendCommentary.setOnClickListener {
            if( !checkUserComments(filteredCommentaries,user_id, productData) ){

                if( !etProductInfoCommentary.text.isNullOrEmpty() && !rbProductInfoRating.rating.toString().isNullOrEmpty() ){

                    CoroutineScope(Dispatchers.IO).launch {
                        createUserCommentary(bearerToken, etProductInfoCommentary.text.toString(), user_id, productData.id, rbProductInfoRating.rating.toInt())
                    }

                }
            }
        }//listener


/*
        imagesList.add(
            CarouselItem(
                imageUrl = "https://cdn-p.smehost.net/sites/7f9737f2506941499994d771a29ad47a/wp-content/uploads/2021/03/Screen-Shot-2021-03-04-at-1.06.09-PM.png"
            )
        )

        imagesList.add(
            CarouselItem(
                imageUrl = "https://cdn.mos.cms.futurecdn.net/iRCQJTSmkYketgvNpSWYNM.jpg"
            )
        )

        icCarousel.addData(imagesList)
*/
        //IMPROVE SPINNERS https://stackoverflow.com/questions/2927012/how-can-i-change-decrease-the-android-spinner-size


        //navigation
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav_view_produt_info.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_item_one->{
                    val intent = Intent(this, IntroDashboardNews::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.nav_item_two->{
                    //saveItemsToFile(cartKey)
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.nav_item_three->{
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



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if( toggle.onOptionsItemSelected(item) ){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadScreenItemsWithProductData(product: DetailProduct, spColors: AutoCompleteTextView, spSizes:AutoCompleteTextView){
        loadArrayOfImages(product)
        tvProductInfoName.text = product.name
        tvProductInfoDescription.text = product.descripcion
        tvProductInfoPrice.text = product.precio.toString()

        loadSizesToSpinner(spSizes, product)
        loadColorsToSpinner(spColors, product)
    }

    private fun loadArrayOfImages(product: DetailProduct ){
        for(k in product.imagen){
            imagesList.add(
                CarouselItem(
                    k.secure_url
                )
            )
        }
        icCarousel.addData(imagesList)
    }

    private fun initUI() {
        drawer = findViewById(R.id.drawerLayoutProductInfo)
        nav_view_produt_info = findViewById(R.id.nav_view_produt_info)

        //carousel of images
        icCarousel = findViewById(R.id.icCarousel)

        tvProductInfoName = findViewById(R.id.tvProductInfoName)
        tvProductInfoDescription = findViewById(R.id.tvProductInfoDescription)
        tvProductInfoPrice = findViewById(R.id.tvProductInfoPrice)

        etProductInfoCommentary = findViewById(R.id.etProductInfoCommentary)
        rbProductInfoRating = findViewById(R.id.rbProductInfoRating)
        btnSendCommentary = findViewById(R.id.btnSendCommentary)
        rvComments = findViewById(R.id.rvComments)
        spProductInfoSizesChoice = findViewById(R.id.spProductInfoSizesChoice)
        spProductInfoColorsChoice = findViewById(R.id.spProductInfoColorsChoice)
    }


    private fun loadSizesToSpinner(spinner: AutoCompleteTextView, detProd: DetailProduct){
        val sizesList = getListNameofSizes(detProd.tallas)
        val adapter = ArrayAdapter(this, R.layout.list_size, sizesList )
        spinner.setAdapter(adapter)
    }

    private fun getListNameofSizes(listSizes: List<Talla>): ArrayList<String>{
        val listaNombres = arrayListOf<String>()
        for(k in listSizes){
            listaNombres.add(k.name)
        }
        return listaNombres
    }


    private fun loadColorsToSpinner(spinner: AutoCompleteTextView, detProd: DetailProduct){
        val colorsList = getListNameOfColores(detProd.colores)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colorsList )
        spinner.setAdapter(adapter)
    }

    private fun getListNameOfColores(listColors: List<Colore>): ArrayList<String>{
        val listaNombres = arrayListOf<String>()
        for(k in listColors){
            listaNombres.add(k.name)
        }
        return listaNombres
    }


    suspend fun createUserCommentary(bearerToken: String, descripcion: String, user_id: String, id_producto:String, calificacion: Int){
        try{
            Log.i("DATA TO CREATE COMMENT ", " desc $descripcion  user $user_id  idprod $id_producto  calif $calificacion")
            val commentaryData = CommentaryData(descripcion, user_id, id_producto, calificacion)

            val resultComment = ResultComment(calificacion, descripcion,"",id_producto, UserId())


            val retrofitCreateNewComment = RetrofitHelper.consumeAPI.createCommentary(bearerToken, commentaryData)

            if(retrofitCreateNewComment.isSuccessful){
                val jsonResponse = retrofitCreateNewComment.body()?.asString

                withContext(Dispatchers.Main){

                    insertNewCommentIntoList(resultComment)
                    Log.i("CREATE COMMENT: ", "COMMENT CREATED SUCCESSFULLY: ${jsonResponse.toString()}")
                }
            }else{
                runOnUiThread {
                    val error = retrofitCreateNewComment.errorBody()?.string()
                    Log.e("ERROR CREATING COMMENT: ", "COULDN'T CREATE NEW COMMENT: ${retrofitCreateNewComment.code()} --**-- $error -*-*-*- ${retrofitCreateNewComment.errorBody().toString()}")
                }
            }

        }catch (e: Exception){
            Log.e("ERROR CONSUMING CREATE COMMENTS API: ", e.message.toString())
        }
    }


    suspend fun bringAllComments(bearerToken: String, onFilterComments: (commentariesList: MutableList<ResultComment>, product: DetailProduct) -> Unit, productData: DetailProduct){
        try {
            val retrofitGetAllComments = RetrofitHelper.consumeAPI.getAllComments(bearerToken)

            if(retrofitGetAllComments.isSuccessful){
                val listResponse = retrofitGetAllComments.body()?.detail
                withContext(Dispatchers.Main){
                    if(listResponse != null){
                        for(k in listResponse){
                            for(comment in k.result){
                                println(comment.calificacion.toString() + " " + comment.user_id + " " + comment.descripcion +  " " + comment.id_producto + "-*-*-*-*-")
                                itemsCommentaries.add(ResultComment(comment.calificacion, comment.descripcion,
                                    comment.id, comment.id_producto, comment.user_id))
                            }
                        }
                        commentsAdapter.notifyDataSetChanged()
                        onFilterComments(itemsCommentaries, productData)
                    }
                }

            }else{
                Log.e("ERROR GETTING COMMENTS: ", "COULDN'T GET COMMENTS: ${retrofitGetAllComments.code()} --**-- ${retrofitGetAllComments.errorBody()?.string()}")
            }
        }catch (e: Exception){
            Log.e("ERROR CONSUMING BRING COMMENTS API: ", e.message.toString())
        }
    }


    private fun checkUserComments(commentariesList: MutableList<ResultComment>, user_id: String, product: DetailProduct): Boolean{

        var hasComments = false
        val productFound =  commentariesList.find { it.id_producto == product.id && it.user_id.id == user_id}
        for(k in commentariesList){
            if(k == productFound){
                hasComments = true
                break
            }
        }


        return hasComments
    }


    private fun filterCommentsOfProduct(commentariesList: MutableList<ResultComment>, product: DetailProduct) {
        println("PRODUCT ID FOR FILTERING: ${product.id}")
        filteredCommentaries = commentariesList.filter { it.id_producto == product.id }.toMutableList()
        Log.i("FILTERED COMMENTS BY PRODUCT: ", filteredCommentaries.toString())

        commentsAdapter.commentsList = filteredCommentaries
        commentsAdapter.notifyDataSetChanged()
    }
    /*
       I SHOULD FILTER BY PRODUCT ID, THE CODE BELOW IS JUST AN EXAMPLE
       val filteredList = commentariesList.filter { it.user_id.id == user_id }
       return filteredList as ArrayList<ResultComment>

       INSTEAD OF (RETURN) = commentariesList.filter { it.user_id.id == user_id } as ArrayList<ResultComment>
    */


    private fun loadIdTokenRoleFromFile(key: String){
        val sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
        user_token = sharedPreferences.getString("USER-TOKEN","").toString()
        user_id =  sharedPreferences.getString("USER-ID","").toString()
        user_rol =  sharedPreferences.getString("USER-ROL","").toString()
        println(user_token + " -- " + user_id + " -- " + user_rol + "III")
    }

    private fun insertNewCommentIntoList(commentaryData: ResultComment){
        filteredCommentaries.add(commentaryData)
        commentsAdapter.commentsList = filteredCommentaries
        commentsAdapter.notifyDataSetChanged()

    }

}