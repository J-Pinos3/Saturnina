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
import android.view.WindowManager
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
import com.example.saturninaapp.adapters.CommentsAdapter
import com.example.saturninaapp.models.CommentaryData
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.models.ResultComment
import com.example.saturninaapp.models.UserId
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GenneralComments : AppCompatActivity() {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_genneral_comments: NavigationView

    lateinit var bottom_nav_genneral_comments: BottomNavigationView

    private lateinit var etGeneralInfoCommentary: EditText
    private lateinit var rbGeneralInfoRating: RatingBar
    private lateinit var btnSendGeneralComment: AppCompatButton

    private var itemsGeneralCommentaries = mutableListOf<ResultComment>()
    private lateinit var rvGeneralComments: RecyclerView
    private lateinit var generalCommentsAdapter: CommentsAdapter

    private val MIN_DESCRIPTION_LENGTH = 10
    private val MAX_DESCRIPTION_LENGTH = 100

    private lateinit var GenneralCommentsItemsCount: TextView

    private var CommentTextWatcher = object: TextWatcher {
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


    private var sharedKey:String = ""
    private var itemsProducts = mutableListOf<DetailProduct>()

    lateinit var nav_heaher_userrolGenneralComents: TextView
    private val ROL_USER: String = "rol:vuqn7k4vw0m1a3wt7fkb"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        initUI()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val user_token = intent.extras?.getString("USER_TOKEN")
        val user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")
        val bearerToken: String = "Bearer $user_token"
        sharedKey = user_id.toString()

        loadItemsFromFile(sharedKey)
        loadCartItemsCount()

        CoroutineScope(Dispatchers.IO).launch {
            BringGeneralComments()
        }

        if(user_rol == ROL_USER){
            nav_heaher_userrolGenneralComents.text = "Cliente"
        }else{
            nav_heaher_userrolGenneralComents.text = "Administrador"
            val flCarritoCompras: FrameLayout = findViewById(R.id.flCarritoCompras)
            flCarritoCompras.visibility = View.GONE
        }


        rvGeneralComments = findViewById(R.id.rvGeneralComments)
        generalCommentsAdapter = CommentsAdapter(itemsGeneralCommentaries)
        rvGeneralComments.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvGeneralComments.adapter = generalCommentsAdapter


        etGeneralInfoCommentary.addTextChangedListener(CommentTextWatcher)
        etGeneralInfoCommentary.setOnFocusChangeListener { view, b ->
            if(b)
                validateDescriptionLength(etGeneralInfoCommentary.text.toString())
        }

        btnSendGeneralComment.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                if (user_id != null) {
                    createGeneralComment(bearerToken, etGeneralInfoCommentary.text.toString(),
                        user_id, rbGeneralInfoRating.rating.toInt())
                }
            }
        }
        //navigation

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nav_view_genneral_comments.setNavigationItemSelectedListener {
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

        bottom_nav_genneral_comments.selectedItemId = R.id.bottom_nav_comments
        bottom_nav_genneral_comments.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_nav_home->{
                    val intent = Intent(this, IntroDashboardNews::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.bottom_nav_categories->{
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.bottom_nav_comments->{ }
            }
            true
        }


        //cart items navigate
        val flCarritoComprasIcon: FrameLayout = findViewById(R.id.flCarritoCompras)
        flCarritoComprasIcon.setOnClickListener {
            //Toast.makeText(this, "Carrito Clicado", Toast.LENGTH_SHORT).show()
            val intent = Intent(applicationContext, CarSalesActivity::class.java)
            intent.putExtra("USER_TOKENTO_PROFILE", user_token)
            intent.putExtra("USER_ID", user_id)
            intent.putExtra("USER_ROL", user_rol)
            startActivity(intent)
        }

    }//ON CREATE


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if( toggle.onOptionsItemSelected(item) ){
            return true
        }
        return super.onOptionsItemSelected(item)
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
                        //Log.d("ALL COMMENTS", itemsGeneralCommentaries.toString())
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


    suspend fun createGeneralComment(bearerToken: String, description: String, user_id: String, calificacion: Int){
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
                    Toast.makeText (this@GenneralComments, msg, Toast.LENGTH_LONG).show()
                }
            }else{
                runOnUiThread {
                    val error = retrofitCreateGComment.errorBody()?.string()
                    val errorBody = error?.let { JSONObject(it) }
                    val detail = errorBody?.opt("detail")
                    var msg = ""
                    when(detail){
                        is JSONObject->{ msg = detail.getString("msg") }

                        is JSONArray ->{
                            val firstError = detail.getJSONObject(0)
                            msg = firstError.getString("msg")
                        }
                    }

                    if(msg == "Token inválido o expirado"){
                        Toast.makeText(this@GenneralComments, "Por favor vuelve a iniciar sesión", Toast.LENGTH_LONG).show()
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText (this@GenneralComments, msg, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }catch (e: Exception){
            Log.e("ERROR CONSUMING CREATE COMMENTS: ", e.message.toString())
        }
    }


    private fun insertCommentIntoList(commentaryData: ResultComment){
        itemsGeneralCommentaries.add(commentaryData)
        generalCommentsAdapter.commentsList = itemsGeneralCommentaries
        generalCommentsAdapter.notifyDataSetChanged()
    }


    //validations
    private fun validateDescriptionLength(comment: String){
        var clickable = true
        if(comment.length !in MIN_DESCRIPTION_LENGTH .. MAX_DESCRIPTION_LENGTH){
            etGeneralInfoCommentary.error = "El comentario debe tener una" +
                    " logitud entre $MIN_DESCRIPTION_LENGTH y $MAX_DESCRIPTION_LENGTH  caracteres"
            clickable = false
        }

        if(clickable){
            btnSendGeneralComment.background = resources.getDrawable(R.drawable.login_register_options_style)
        }else{
            btnSendGeneralComment.background =  resources.getDrawable(R.drawable.disabled_buttons_style)
        }

        btnSendGeneralComment.isClickable = clickable
    }


    private fun disableClicCreateComment(comment: String) {
        btnSendGeneralComment.isClickable = !comment.isNullOrEmpty()

        when(btnSendGeneralComment.isClickable){
            true->{
                btnSendGeneralComment.background = resources.getDrawable(R.drawable.login_register_options_style)
            }
            false->{
                btnSendGeneralComment.background =  resources.getDrawable(R.drawable.disabled_buttons_style)
            }
        }
    }




    private fun initUI() {
        //navigation
        drawer = findViewById(R.id.drawerLayoutGenneralComments)
        nav_view_genneral_comments = findViewById(R.id.nav_view_genneral_comments)

        //bottom navigation
        bottom_nav_genneral_comments = findViewById(R.id.bottom_nav_genneral_comments)

        //cart items count
        GenneralCommentsItemsCount = findViewById(R.id.action_cart_count)

        generalCommentsAdapter = CommentsAdapter(mutableListOf<ResultComment>())
        etGeneralInfoCommentary = findViewById(R.id.etGeneralInfoCommentary)
        rbGeneralInfoRating = findViewById(R.id.rbGeneralInfoRating)
        btnSendGeneralComment = findViewById(R.id.btnSendGeneralComment)

        nav_heaher_userrolGenneralComents =  nav_view_genneral_comments.getHeaderView(0).findViewById(R.id.nav_heaher_userrol)
    }


    private fun loadCartItemsCount(){
        var suma: Int = 0
        for(k in itemsProducts){
            suma += k.contador
        }
        GenneralCommentsItemsCount.text = suma.toString()
    }


    private fun loadItemsFromFile(Key: String){
        var sharedPreferences: SharedPreferences = getSharedPreferences(Key, MODE_PRIVATE)
        val gson = Gson()

        val jsonString = sharedPreferences.getString(Key,"")
        val type = object : TypeToken<MutableList<DetailProduct>>() {}.type
        itemsProducts = (gson.fromJson(jsonString, type) ?: mutableListOf<DetailProduct>() )

        //println("CARRASO de productos: $itemsProducts")
    }


}