package com.example.saturninaapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
import com.example.saturninaapp.models.ResultComment
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val itemsCommentaries = mutableListOf<ResultComment>()

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
        commentsAdapter = CommentsAdapter(itemsCommentaries)
        rvComments.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvComments.adapter = commentsAdapter

        val bearerToken = "Bearer $user_token"

        CoroutineScope(Dispatchers.IO).launch {
            bringAllComments(bearerToken)
        }

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



    suspend fun createUserCommentary(bearerToken: String, descripcion: String, user_id: String, id_producto:String, calificacion: Int){
        try{
            val retrofitCreateNewComment = RetrofitHelper.consumeAPI.createCommentary(bearerToken, descripcion, user_id, id_producto, calificacion)

            if(retrofitCreateNewComment.isSuccessful){
                val jsonResponse = retrofitCreateNewComment.body()

                withContext(Dispatchers.Main){
                    Log.i("CREATE COMMENT: ", "COMMENT CREATED SUCCESSFULLY: ${jsonResponse.toString()}")
                }
            }else{
                runOnUiThread {
                    val error = retrofitCreateNewComment.errorBody()?.charStream().toString()
                    Log.e("ERROR CREATING COMMENT: ", "COULDN'T CREATE NEW COMMENT: ${retrofitCreateNewComment.code()} --**-- $error")
                }
            }

        }catch (e: Exception){
            Log.e("ERROR CONSUMING CREATE COMMENTS API: ", e.message.toString())
        }
    }


    suspend fun bringAllComments(bearerToken: String){
        try {
            val retrofitGetAllComments = RetrofitHelper.consumeAPI.getAllComments(bearerToken)

            if(retrofitGetAllComments.isSuccessful){
                val listResponse = retrofitGetAllComments.body()?.detail
                withContext(Dispatchers.Main){
                    if(listResponse != null){
                        for(k in listResponse){
                            for(comment in k.result){
                                itemsCommentaries.add(ResultComment(comment.calificacion, comment.descripcion,
                                    comment.id, comment.id_producto, comment.user_id))
                            }
                        }
                        commentsAdapter.notifyDataSetChanged()
                    }
                }

            }else{
                Log.e("ERROR GETTING COMMENTS: ", "COULDN'T GET COMMENTS: ${retrofitGetAllComments.code()} --**-- ${retrofitGetAllComments.errorBody().toString()}")
            }
        }catch (e: Exception){
            Log.e("ERROR CONSUMING BRING COMMENTS API: ", e.message.toString())
        }
    }

    private fun loadIdTokenRoleFromFile(key: String){
        val sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
        user_token = sharedPreferences.getString("USER-TOKEN","").toString()
        user_id =  sharedPreferences.getString("USER-ID","").toString()
        user_rol =  sharedPreferences.getString("USER-ROL","").toString()
    }

}