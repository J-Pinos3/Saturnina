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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.example.saturninaapp.models.Colore
import com.example.saturninaapp.models.CommentaryData
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.models.ResultComment
import com.example.saturninaapp.models.Talla
import com.example.saturninaapp.models.UserId
import com.example.saturninaapp.util.RetrofitHelper
import com.example.saturninaapp.util.UtilClasses
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.imaginativeworld.whynotimagecarousel.CarouselItem
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.json.JSONArray
import org.json.JSONObject

class ShowProductInfo : AppCompatActivity(), UtilClasses {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nav_view_produt_info: NavigationView

    lateinit var bottom_nav_product_info: BottomNavigationView

    private val imagesList = mutableListOf<CarouselItem>()
    private lateinit var icCarousel: ImageCarousel

    private lateinit var tvProductInfoName: TextView
    private lateinit var tvProductInfoDescription: TextView
    private lateinit var tvProductInfoPrice: TextView

    private lateinit var tilProductInfoSizes: TextInputLayout
    private lateinit var tilProductInfoColors: TextInputLayout


    private lateinit var etProductInfoCommentary: EditText

    private lateinit var rbProductInfoRating: RatingBar

    private lateinit var btnSendCommentary: AppCompatButton

    private lateinit var rvComments: RecyclerView
    private lateinit var commentsAdapter: CommentsAdapter
    private var itemsCommentaries = mutableListOf<ResultComment>()
    private var filteredCommentaries = mutableListOf<ResultComment>()

    private lateinit var spProductInfoSizesChoice: AutoCompleteTextView
    private lateinit var spProductInfoColorsChoice: AutoCompleteTextView

    private lateinit var cartSalesItemsCount: TextView

    private lateinit var btnAddProductInfoToCart: View
    private var cartItems = mutableListOf<DetailProduct>()

    private var fileKey: String = "user_data"
    private var user_token: String = ""
    private var user_id: String = ""
    private var user_rol: String = ""
    private var cartKey = ""


    private val MIN_LENGTH_DESCRIPTION = 10
    private val MAX_LENGTH_DESCRIPTION = 100

    private var CommentTextWatcher = object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val comment = etProductInfoCommentary.text.toString()

            disableButtonCreateComment(comment)
        }

    }

    lateinit var nav_heaher_userrolProductInfo: TextView
    private val ROL_USER: String = "rol:vuqn7k4vw0m1a3wt7fkb"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_product_info)
        initUI()
        loadIdTokenRoleFromFile(fileKey)

        loadItemsFromFile(cartKey)
        loadItemsCount()

        val bearerToken = "Bearer $user_token"
        val productData = intent.getSerializableExtra("PRODUCT_DATA") as DetailProduct
        CoroutineScope(Dispatchers.IO).launch {
            bringAllComments(bearerToken, { itemsCommentaries, productData ->  filterCommentsOfProduct(itemsCommentaries, productData) }, productData )
        }

        if(user_rol == ROL_USER){
            nav_heaher_userrolProductInfo.text = "Cliente"
        }else{
            nav_heaher_userrolProductInfo.text = "Administrador"
            val flCarritoCompras: FrameLayout = findViewById(R.id.flCarritoCompras)
            flCarritoCompras.visibility = View.GONE

            btnAddProductInfoToCart.visibility = View.GONE
        }

        val hasColors = onColorSelected(spProductInfoColorsChoice, productData)
        if(hasColors == false){
            tilProductInfoColors.visibility = View.GONE
        }else{
            tilProductInfoColors.visibility = View.VISIBLE
        }

        val hasSizes = onSizeSelected(spProductInfoSizesChoice, productData)
        if(hasSizes == false){
            tilProductInfoSizes.visibility = View.GONE
        }else{
            tilProductInfoSizes.visibility = View.VISIBLE
        }


        loadScreenItemsWithProductData(productData, spProductInfoColorsChoice, spProductInfoSizesChoice)



        commentsAdapter = CommentsAdapter(itemsCommentaries)
        rvComments.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvComments.adapter = commentsAdapter


        btnSendCommentary.setOnClickListener {
            if( !checkUserComments(filteredCommentaries,user_id, productData) ){

                if( !etProductInfoCommentary.text.isNullOrEmpty() && !rbProductInfoRating.rating.toString().isNullOrEmpty() ){

                    CoroutineScope(Dispatchers.IO).launch {
                        createUserCommentary(bearerToken, etProductInfoCommentary.text.toString(), user_id, productData.id, rbProductInfoRating.rating.toInt())
                    }

                }

            }else{
                Toast.makeText(this, "Ya haz hecho un comentario", Toast.LENGTH_LONG).show()
            }


        }//listener


        etProductInfoCommentary.addTextChangedListener(CommentTextWatcher)
        etProductInfoCommentary.setOnFocusChangeListener { view, b ->
            if(b)
                validateDescriptionLength(etProductInfoCommentary.text.toString())
        }

        spProductInfoSizesChoice.onItemClickListener =
                AdapterView.OnItemClickListener {
                        adapterView, view, position, id ->
                    productData.tallaSeleccionada = adapterView.getItemAtPosition(position).toString()
                    spProductInfoSizesChoice.post{
                        spProductInfoSizesChoice.text = Editable.Factory.getInstance().newEditable(productData.tallaSeleccionada)
                    }
                }


        spProductInfoColorsChoice.onItemClickListener =
                AdapterView.OnItemClickListener {
                        adapterView, view, position, id ->
                    productData.colorSeleccionado = adapterView.getItemAtPosition(position).toString()
                    spProductInfoColorsChoice.post{
                        spProductInfoColorsChoice.text = Editable.Factory.getInstance().newEditable(productData.colorSeleccionado)
                    }
                }
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


        btnAddProductInfoToCart.setOnClickListener {
            onItemClothSelected(productData)
        }


        //navigation
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav_view_produt_info.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_item_one->{
                    saveItemsToFile(cartKey)
                    val intent = Intent(this, IntroDashboardNews::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }


                R.id.nav_item_three->{
                    saveItemsToFile(cartKey)
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("USER_TOKEN_PROFILE", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }


                R.id.nav_item_five ->{
                    saveItemsToFile(cartKey)
                    val intent = Intent(this, ManagementOptionsActivity::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.nav_item_six ->{
                    saveItemsToFile(cartKey)
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }

        bottom_nav_product_info.setOnNavigationItemSelectedListener{

            when(it.itemId){
                R.id.bottom_nav_home->{
                    saveItemsToFile(cartKey)
                    val intent = Intent(this, IntroDashboardNews::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.bottom_nav_categories->{
                    saveItemsToFile(cartKey)
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.bottom_nav_comments->{
                    saveItemsToFile(cartKey)
                    val intent = Intent(this, GenneralComments::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }
            }
            true
        }


        val flCarritoCOmpras: FrameLayout = findViewById(R.id.flCarritoCompras)
        flCarritoCOmpras.setOnClickListener {
            saveItemsToFile(cartKey)

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

        bottom_nav_product_info = findViewById(R.id.bottom_nav_product_info)

        //carousel of images
        icCarousel = findViewById(R.id.icCarousel)

        tvProductInfoName = findViewById(R.id.tvProductInfoName)
        tvProductInfoDescription = findViewById(R.id.tvProductInfoDescription)
        tvProductInfoPrice = findViewById(R.id.tvProductInfoPrice)

        tilProductInfoSizes = findViewById(R.id.tilProductInfoSizes)
        tilProductInfoColors = findViewById(R.id.tilProductInfoColors)

        etProductInfoCommentary = findViewById(R.id.etProductInfoCommentary)
        rbProductInfoRating = findViewById(R.id.rbProductInfoRating)
        btnSendCommentary = findViewById(R.id.btnSendCommentary)
        rvComments = findViewById(R.id.rvComments)
        spProductInfoSizesChoice = findViewById(R.id.spProductInfoSizesChoice)
        spProductInfoColorsChoice = findViewById(R.id.spProductInfoColorsChoice)

        btnAddProductInfoToCart = findViewById(R.id.btnAddProductInfoToCart)

        cartSalesItemsCount = findViewById(R.id.action_cart_count)

        nav_heaher_userrolProductInfo = nav_view_produt_info.getHeaderView(0).findViewById(R.id.nav_heaher_userrol)
    }


    private fun disableButtonCreateComment(comment: String) {
        btnSendCommentary.isClickable = !comment.isNullOrEmpty()

        when(btnSendCommentary.isClickable){
            true->{
                btnSendCommentary.background = resources.getDrawable(R.drawable.login_register_options_style)
            }

            false->{
                btnSendCommentary.background = resources.getDrawable(R.drawable.disabled_buttons_style)
            }
        }
    }


    private fun validateDescriptionLength(comment: String) {
        var clickable = true

        if(comment.length !in MIN_LENGTH_DESCRIPTION .. MAX_LENGTH_DESCRIPTION){
            etProductInfoCommentary.error = "El comentario debe tener una" +
                    " logitud entre $MIN_LENGTH_DESCRIPTION y $MAX_LENGTH_DESCRIPTION  caracteres"
            clickable = false
        }

        if(clickable){
            btnSendCommentary.background = resources.getDrawable(R.drawable.login_register_options_style)
        }else{
            btnSendCommentary.background = resources.getDrawable(R.drawable.disabled_buttons_style)
        }

        btnSendCommentary.isClickable = clickable
    }


    override fun onItemClothSelected(product: DetailProduct) {
        val existingProduct = cartItems.find { it.id == product.id }

        if(existingProduct == null){
            product.contador++
            cartItems.add(product)

            increaseCartItemsInfoCount()//AUMENTAMOS EL CONTADOR PARA EL NUEVO PRODUCTO
        }else{
            existingProduct.contador++
            increaseCartItemsInfoCount()
        }
    }

    private fun increaseCartItemsInfoCount(){
        cartSalesItemsCount.text = ( cartSalesItemsCount.text.toString().toInt() + 1  ).toString()
    }


    override fun onItemDeleteSelected(product: DetailProduct) { //NOTHING TO DELETE UNLESS ITS CART SALES ACTIVITY
    }

    override fun onColorSelected(spinner: AutoCompleteTextView, product: DetailProduct): Boolean {
        val listofColors = getListNameOfColores(product.colores)
        var hasItems = true

        if(listofColors.isNotEmpty()){
            spinner.isEnabled = true
            if(product.colorSeleccionado.isNotEmpty()){
                var indiceColor = listofColors.find { it == product.colorSeleccionado }
                spinner.setText(indiceColor, false)
            }else{
                spinner.setText( listofColors.elementAt(0), false )
            }
        }else{
            hasItems = false
            spinner.setText( "N/A", false )
            spinner.isEnabled = false
        }

        return hasItems
    }

    override fun onSizeSelected(spinner: AutoCompleteTextView, product: DetailProduct): Boolean {
        val listofSizes = getListNameofSizes(product.tallas)
        var hasItems = true

        if(listofSizes.isNotEmpty()){
            spinner.isEnabled = true
            if(product.tallaSeleccionada.isNotEmpty()){
                var indiceTalla = listofSizes.find { it == product.tallaSeleccionada }
                spinner.setText(indiceTalla, false)
            }else{
                spinner.setText( listofSizes.elementAt(0), false )
            }

        }else{
            hasItems = false
            //tilProductInfoSizes.visibility = View.GONE
            spinner.setText( "N/A", false )
            spinner.isEnabled = false
        }

        return hasItems
    }


    private fun loadSizesToSpinner(spinner: AutoCompleteTextView, detProd: DetailProduct){
        val sizesList = getListNameofSizes(detProd.tallas)
        val adapter = ArrayAdapter(this, R.layout.list_size, sizesList )
        spinner.setAdapter(adapter)
    }


    private fun getListNameofSizes(listSizes: List<Talla>?): ArrayList<String>{
        val listaNombres = arrayListOf<String>()
        if (listSizes != null) {
            for(k in listSizes){
                listaNombres.add(k.name)
            }
        }
        return listaNombres
    }


    private fun loadColorsToSpinner(spinner: AutoCompleteTextView, detProd: DetailProduct){
        val colorsList = getListNameOfColores(detProd.colores)
        val adapter = ArrayAdapter(this, R.layout.list_size, colorsList )
        spinner.setAdapter(adapter)
    }


    private fun getListNameOfColores(listColors: List<Colore>?): ArrayList<String>{
        val listaNombres = arrayListOf<String>()
        if (listColors != null) {
            for(k in listColors){
                listaNombres.add(k.name)
            }
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
                val jsonResponse = retrofitCreateNewComment.body()?.toString()
                val jsonObject = jsonResponse?.let { JSONObject(it) }
                val detailObject = jsonObject?.getJSONObject("detail")
                val msg = detailObject?.getString("msg")

                print("awuuii verga1")
                withContext(Dispatchers.Main){

                    insertNewCommentIntoList(resultComment)
                    Toast.makeText (this@ShowProductInfo, msg, Toast.LENGTH_LONG).show()
                    Log.i("CREATE COMMENT: ", "COMMENT CREATED SUCCESSFULLY: $msg")
                }
            }else{
                print("awuuii verga3")
                runOnUiThread {
                    print("awuuii verga2")
                    val error = retrofitCreateNewComment.errorBody()?.string()
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
                    Toast.makeText (this@ShowProductInfo, msg, Toast.LENGTH_LONG).show()
                    //Log.e("ERROR CREATING COMMENT: ", "COULDN'T CREATE NEW COMMENT: ${retrofitCreateNewComment.code()} --**-- $msg  --**-- $error -*-*-*- ${retrofitCreateNewComment.errorBody().toString()}")
                }
            }

        }catch (e: Exception){
            Log.e("ERROR CREATE COMMENTS: ", e.message.toString())
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
        cartKey = user_id
        println(user_token + " -- " + user_id + " -- " + user_rol + "III")
    }


    private fun loadItemsFromFile(Key: String){
        var sharedPreferences: SharedPreferences = getSharedPreferences(Key, MODE_PRIVATE)
        val gson = Gson()

        val jsonString = sharedPreferences.getString(Key,"")
        val type = object : TypeToken< MutableList<DetailProduct> >(){}.type
        cartItems = (gson.fromJson(jsonString, type)) ?: mutableListOf<DetailProduct>()
    }

    private fun saveItemsToFile(Key: String){
        var sharedPreferences: SharedPreferences = getSharedPreferences(Key, MODE_PRIVATE)
        val gson = Gson()
        val editor = sharedPreferences.edit()

        val jsonString = gson.toJson(cartItems)
        editor.putString(Key, jsonString)
        editor.apply()
    }

    private fun loadItemsCount(){
        var suma = 0
        for(k in cartItems){
            suma += k.contador
        }
        cartSalesItemsCount.text = suma.toString()
    }


    private fun insertNewCommentIntoList(commentaryData: ResultComment){
        filteredCommentaries.add(commentaryData)
        commentsAdapter.commentsList = filteredCommentaries
        commentsAdapter.notifyDataSetChanged()

    }

}