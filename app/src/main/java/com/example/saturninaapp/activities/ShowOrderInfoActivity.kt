package com.example.saturninaapp.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import android.Manifest
import android.os.Build
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.core.view.isVisible
import com.example.saturninaapp.R
import com.example.saturninaapp.models.OrderResult
import com.example.saturninaapp.models.OrderStatusData
import com.example.saturninaapp.models.itemProduct
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.lang.Exception

class ShowOrderInfoActivity : AppCompatActivity() {

    private lateinit var btnBacktoSalesActivity: ImageButton
    private lateinit var btnUpdateUserOrderData: AppCompatButton

    lateinit var bottom_nav_order_info: BottomNavigationView

    private lateinit var ivBillOrderImage: ImageView


    private lateinit var tvOrderInfoDate: TextView
    private lateinit var tvOrderInfoFirstName: EditText
    private lateinit var tvOrderInfoLastName: EditText
    private lateinit var tvOrderInfoEmail: EditText
    private lateinit var tvOrderInfoAddress: EditText
    private lateinit var tvOrderInfoCellPhone: EditText
    private lateinit var tvOrderInfoDescription: EditText
    private lateinit var tvOrderInfoTotalPrice: TextView
    private lateinit var tvOrderInfoProductName: TextView
    private lateinit var tvOrderInfoSelectedSize: TextView
    private lateinit var tvOrderInfoSelectedColor: TextView
    private lateinit var tvOrderInfoQuantity: TextView
    private lateinit var tvOrderInfoUnitPrice: TextView

    private lateinit var etStatusDescription: TextView
    private lateinit var spOrderStatusChoice: AutoCompleteTextView

    private val MIN_LENGTH_NAME = 3
    private val MAX_LENGTH_NAME = 10

    private val MIN_LENGTH_CELLPHONE = 10
    private val MAX_LENGTH_CELLPHONE = 10

    private val MIN_LENGTH_DESCRIPTION = 10
    private val MAX_LENGTH_DESCRIPTION = 100

    private var OrderTextWatcher = object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val firstName: String = tvOrderInfoFirstName.text.toString()
            val lastName: String = tvOrderInfoLastName.text.toString()
            val email: String = tvOrderInfoEmail.text.toString()
            val address: String = tvOrderInfoAddress.text.toString()
            val phone: String = tvOrderInfoCellPhone.text.toString()
            val description: String = tvOrderInfoDescription.text.toString()
            val orderStatus: String = etStatusDescription.text.toString()

            //validate if is empty
            disableClicOnUpdateOrder(firstName, lastName, email, address, phone, description)

            //validateInputLength(firstName, lastName, email, address, phone, description)
        }

    }

    private val ROL_USER: String = "rol:vuqn7k4vw0m1a3wt7fkb"
    private val ROL_ADMIN: String = "rol:74rvq7jatzo6ac19mc79"

    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: Int = 123
    private var bearerToken: String = ""
    private var user_id: String = ""
    private var user_rol: String = ""
    private lateinit var orderSelectedInfo: OrderResult

    private val pickImage = registerForActivityResult( ActivityResultContracts.StartActivityForResult() ){
        if(it.resultCode == RESULT_OK){
            val data = it.data
            val imgUri = data?.data


            CoroutineScope(Dispatchers.IO).launch {
                if(imgUri != null){
                    val file = uriToFile(imgUri)
                    if( file != null && file.exists() ){
                        println("TOKEN IS LIKE? $bearerToken")
                        updateUserOrder(bearerToken, tvOrderInfoFirstName.text.toString(), tvOrderInfoLastName.text.toString(),
                            tvOrderInfoAddress.text.toString(), tvOrderInfoEmail.text.toString(), tvOrderInfoCellPhone.text.toString(),
                            orderSelectedInfo.id_orden.id, file)
                    }
                }
            }

        }

    }

    private lateinit var statusList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_order_info)
        initUI()


        val user_token = intent.extras?.getString("USER_TOKEN")
        user_id = intent.extras?.getString("USER_ID").toString()
        user_rol = intent.extras?.getString("USER_ROL").toString()
        bearerToken = "Bearer $user_token"
        println("token del usuario: ${bearerToken}")
        orderSelectedInfo = intent.getSerializableExtra("ORDER_SELECTED") as OrderResult
        fillViewsWithOrderInfo(orderSelectedInfo)
        when(user_rol){
            ROL_USER ->{
                spOrderStatusChoice.isEnabled = false
                etStatusDescription.isEnabled = false
            }

            ROL_ADMIN ->{
                spOrderStatusChoice.isEnabled = true
                tvOrderInfoFirstName.isEnabled = false
                tvOrderInfoLastName.isEnabled = false
                tvOrderInfoEmail.isEnabled = false
                tvOrderInfoAddress.isEnabled = false
                tvOrderInfoCellPhone.isEnabled = false
                tvOrderInfoDescription.isEnabled = false
            }
        }
        disableClicOnUpdateOrder()

        tvOrderInfoFirstName.addTextChangedListener(OrderTextWatcher)
        tvOrderInfoLastName.addTextChangedListener(OrderTextWatcher)
        tvOrderInfoEmail.addTextChangedListener(OrderTextWatcher)
        tvOrderInfoAddress.addTextChangedListener(OrderTextWatcher)
        tvOrderInfoCellPhone.addTextChangedListener(OrderTextWatcher)
        tvOrderInfoDescription.addTextChangedListener(OrderTextWatcher)


        tvOrderInfoFirstName.setOnFocusChangeListener { view, b ->
            if(b)
                validateInputLength(tvOrderInfoFirstName.text.toString(), tvOrderInfoLastName.text.toString(), tvOrderInfoEmail.text.toString(),
                    tvOrderInfoAddress.text.toString(), tvOrderInfoCellPhone.text.toString(), tvOrderInfoDescription.text.toString(), etStatusDescription.text.toString())
        }

        tvOrderInfoLastName.setOnFocusChangeListener { view, b ->
            if(b)
                validateInputLength(tvOrderInfoFirstName.text.toString(), tvOrderInfoLastName.text.toString(), tvOrderInfoEmail.text.toString(),
                    tvOrderInfoAddress.text.toString(), tvOrderInfoCellPhone.text.toString(), tvOrderInfoDescription.text.toString(), etStatusDescription.text.toString())
        }

        tvOrderInfoEmail.setOnFocusChangeListener { view, b ->
            if(b)
                validateInputLength(tvOrderInfoFirstName.text.toString(), tvOrderInfoLastName.text.toString(), tvOrderInfoEmail.text.toString(),
                    tvOrderInfoAddress.text.toString(), tvOrderInfoCellPhone.text.toString(), tvOrderInfoDescription.text.toString(), etStatusDescription.text.toString())
        }

        tvOrderInfoAddress.setOnFocusChangeListener { view, b ->
            if(b)
                validateInputLength(tvOrderInfoFirstName.text.toString(), tvOrderInfoLastName.text.toString(), tvOrderInfoEmail.text.toString(),
                    tvOrderInfoAddress.text.toString(), tvOrderInfoCellPhone.text.toString(), tvOrderInfoDescription.text.toString(), etStatusDescription.text.toString())
        }

        tvOrderInfoCellPhone.setOnFocusChangeListener { view, b ->
            if(b)
                validateInputLength(tvOrderInfoFirstName.text.toString(), tvOrderInfoLastName.text.toString(), tvOrderInfoEmail.text.toString(),
                    tvOrderInfoAddress.text.toString(), tvOrderInfoCellPhone.text.toString(), tvOrderInfoDescription.text.toString(), etStatusDescription.text.toString())
        }

        tvOrderInfoDescription.setOnFocusChangeListener { view, b ->
            if(b)
                validateInputLength(tvOrderInfoFirstName.text.toString(), tvOrderInfoLastName.text.toString(), tvOrderInfoEmail.text.toString(),
                    tvOrderInfoAddress.text.toString(), tvOrderInfoCellPhone.text.toString(), tvOrderInfoDescription.text.toString(), etStatusDescription.text.toString())
        }

        etStatusDescription.setOnFocusChangeListener { view, b ->
            if(b)
                validateInputLength(tvOrderInfoFirstName.text.toString(), tvOrderInfoLastName.text.toString(), tvOrderInfoEmail.text.toString(),
                    tvOrderInfoAddress.text.toString(), tvOrderInfoCellPhone.text.toString(), tvOrderInfoDescription.text.toString(), etStatusDescription.text.toString())
        }



        spOrderStatusChoice.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            if(statusList.isNotEmpty()){
                spOrderStatusChoice.isEnabled = true
                orderSelectedInfo.status = adapterView.getItemAtPosition(i).toString()

                spOrderStatusChoice.text = Editable.Factory.getInstance().newEditable(orderSelectedInfo.status)
                spOrderStatusChoice.setText(orderSelectedInfo.status, false)
            }else{
                spOrderStatusChoice.setText("N/A", false)
                spOrderStatusChoice.isEnabled = false
            }

        }



        btnBacktoSalesActivity.setOnClickListener {
            if (user_id != null && user_token != null && user_rol != null) {
                navigateBacktoMySalesActivity(user_token, user_id, user_rol)
            }
        }

        btnUpdateUserOrderData.setOnClickListener {
            if(user_rol == ROL_USER ){


                ActivityCompat.requestPermissions(this@ShowOrderInfoActivity,
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                    }else{ arrayOf( Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                    },
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
            }

            if(user_rol == ROL_ADMIN){

                val orderStatus = getOrderStatusFromUI()
                CoroutineScope(Dispatchers.IO).launch {
                    updateOrderStatus(bearerToken, orderStatus, orderSelectedInfo.id)
                }
            }


        }


        bottom_nav_order_info.setOnNavigationItemSelectedListener {
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

    }//ON CREATE



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if(user_rol == ROL_USER){
                    val selectBillImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    pickImage.launch(selectBillImage)
                }

                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "NO Permission Granted", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun disableClicOnUpdateOrder(){
        btnUpdateUserOrderData.isClickable = false
        btnUpdateUserOrderData.background = resources.getDrawable(R.drawable.disabled_buttons_style)
    }


    private fun disableClicOnUpdateOrder(firstName: String, lastName:String, email:String,
                address: String, phone: String, description: String){

        btnUpdateUserOrderData.isClickable = ( !firstName.isNullOrEmpty() ) && ( !lastName.isNullOrEmpty() )
                && ( !email.isNullOrEmpty() ) && (!address.isNullOrEmpty())  && ( !phone.isNullOrEmpty() )
                && ( !description.isNullOrEmpty() )

        when(btnUpdateUserOrderData.isClickable){
            true -> {
                btnUpdateUserOrderData.background =  resources.getDrawable(R.drawable.login_register_options_style)
            }

            false -> {
                btnUpdateUserOrderData.background = resources.getDrawable(R.drawable.disabled_buttons_style)
            }
        }

    }

    private fun validateInputLength(firstName: String, lastName: String, email: String, address: String, phone: String, description: String, status: String) {

        var clickable = true


        if( firstName.length  !in MIN_LENGTH_NAME .. MAX_LENGTH_NAME){
            tvOrderInfoFirstName.error = "El nombre debe tener una longitud entre $MAX_LENGTH_NAME y $MAX_LENGTH_NAME caracteres"
            clickable = false
        }


        if( lastName.length !in MIN_LENGTH_NAME ..  MAX_LENGTH_NAME){
            tvOrderInfoLastName.error="El apellido debe tener una longitud entre $MIN_LENGTH_NAME y $MAX_LENGTH_NAME caracteres"
            clickable = false
        }


        if(email.isEmpty()){
            tvOrderInfoEmail.error = "Debe ingresar el correo"
            clickable = false
        }

        if( !email.contains("@") || !email.contains(".") ){
            tvOrderInfoEmail.error = "El correo debe tener @ y al menos un ."
            clickable = false
        }

        if( address.isEmpty() ){
            tvOrderInfoAddress.error = "Debe ingresar un dirección de domicilio"
            clickable = false
        }

        if(phone.length != MIN_LENGTH_CELLPHONE){
            tvOrderInfoCellPhone.error="El teléfono debe tener una longitud de $MIN_LENGTH_CELLPHONE  caracteres"
            clickable = false
        }

        if(description.length !in MIN_LENGTH_DESCRIPTION .. MAX_LENGTH_DESCRIPTION){
            tvOrderInfoDescription.error ="El descripción debe tener una longitud entre $MIN_LENGTH_DESCRIPTION y $MAX_LENGTH_DESCRIPTION  caracteres"
            clickable = false
        }

        if(status.isEmpty()){
            etStatusDescription.error = "Debe ingresar el estado de la orden"
            clickable = false
        }

        if(clickable){
            btnUpdateUserOrderData.background =  resources.getDrawable(R.drawable.login_register_options_style)
        }else{
            btnUpdateUserOrderData.background = resources.getDrawable(R.drawable.disabled_buttons_style)
        }

        btnUpdateUserOrderData.isEnabled = clickable
    }



    suspend fun updateOrderStatus( bearerToken: String, orderStatusData: OrderStatusData, order_id: String ){

        println("ORDER ID: $order_id")
        try {
            println("ORDER ID: $order_id")
            val retrofitUpdateUserStatus = RetrofitHelper.consumeAPI.uptdateOrderStatus(bearerToken, orderStatusData, order_id)
            if(retrofitUpdateUserStatus.isSuccessful){

                val jsonResponse = retrofitUpdateUserStatus.body()?.toString()
                val jsonObject = jsonResponse?.let { JSONObject(it) }
                val detailObject = jsonObject?.getJSONObject("detail")
                val msg = detailObject?.getString("msg")
                Toast.makeText(this@ShowOrderInfoActivity, msg, Toast.LENGTH_LONG).show()
                withContext(Dispatchers.IO){
                    Log.i("UPDATE ORDER", "ORDER STATUS UPDATED SUCCESSFULLY $msg")
                }

            }else{
                runOnUiThread {
                    val error = retrofitUpdateUserStatus.errorBody()?.string()
                    Log.e("ERROR UPDATING ORDER", "${retrofitUpdateUserStatus.code()}  -- ${error.toString()}")
                }
            }

        }catch (e: Exception){
            Log.e("ERROR UPDATE STATUS API: ", e.printStackTrace().toString())
        }
    }

    private fun getOrderStatusFromUI(): OrderStatusData{
        var status = ""
        /*
        spOrderStatusChoice.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            if(statusList.isNotEmpty()){

                status = adapterView.getItemAtPosition(i).toString()
            }else{
                status = "N/A"
            }

        }
        */
        if( spOrderStatusChoice.text.toString().isNotEmpty() ){
            status = spOrderStatusChoice.text.toString()
        }


        Log.i("DATA FOR STATUS"," data -- $status --  ${etStatusDescription.text}")
        return OrderStatusData( status, etStatusDescription.text.toString() )
    }


    suspend fun updateUserOrder(bearerToken: String, nombre:String, apellido: String,
                                direccion: String, email: String, telefono: String, order_id: String, image: File){

        try {

            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), image)
            val filepart = MultipartBody.Part.createFormData(
                "transfer_image",
                image.name,
                requestFile
            )
            Log.i("iimage loaded:","${image.absoluteFile}")

            val dataJson = JsonObject()

            dataJson.addProperty("nombre", nombre)
            dataJson.addProperty("apellido", apellido)
            dataJson.addProperty("direccion", direccion)
            dataJson.addProperty("email", email)
            dataJson.addProperty("telefono", telefono)

            val dataBody = RequestBody.create(MediaType.parse("application/json"), dataJson.toString())


            val retrofitUpdateUserComment = RetrofitHelper.consumeAPI.updateUserOrder(
                bearerToken, dataBody, filepart, order_id)


            if(retrofitUpdateUserComment.isSuccessful){

                val jsonResponse = retrofitUpdateUserComment.body()?.toString()
                val jsonObject = jsonResponse?.let { JSONObject(it) }
                val detailObject = jsonObject?.getJSONObject("detail")
                val msg = detailObject?.getString("msg")

                Toast.makeText(this@ShowOrderInfoActivity, msg, Toast.LENGTH_LONG).show()
                withContext(Dispatchers.Main){
                    Log.i("UPDATE ORDER", "ORDER UPDATED SUCCESSFULLY $msg")
                }

            }else{
                runOnUiThread {
                    val error = retrofitUpdateUserComment.errorBody()?.string()
                    Log.e("ERROR UPDATING ORDER", "${retrofitUpdateUserComment.code()}  -- ${error.toString()}")
                }
            }


        }catch (e: Exception){
            Log.e("ERROR CONSUMING COMMENTS API: ", e.printStackTrace().toString())
        }

    }



    private fun fillViewsWithOrderInfo (orderSelectedInfo: OrderResult){
        fillImageOrderView(ivBillOrderImage, orderSelectedInfo)

        tvOrderInfoDate.text = orderSelectedInfo.fecha
        tvOrderInfoFirstName.post {  tvOrderInfoFirstName.text = Editable.Factory.getInstance().newEditable( orderSelectedInfo.id_orden.nombre )  }
        tvOrderInfoLastName.post { tvOrderInfoLastName.text = Editable.Factory.getInstance().newEditable( orderSelectedInfo.id_orden.apellido ) }
        tvOrderInfoEmail.post { tvOrderInfoEmail.text = Editable.Factory.getInstance().newEditable( orderSelectedInfo.id_orden.email ) }
        tvOrderInfoAddress.post { tvOrderInfoAddress.text = Editable.Factory.getInstance().newEditable( orderSelectedInfo.id_orden.direccion ) }
        tvOrderInfoCellPhone.post { tvOrderInfoCellPhone.text = Editable.Factory.getInstance().newEditable( orderSelectedInfo.id_orden.telefono ) }
        tvOrderInfoDescription.post { tvOrderInfoDescription.text = Editable.Factory.getInstance().newEditable( orderSelectedInfo.id_orden.descripcion ) }

        etStatusDescription.post { etStatusDescription.text = Editable.Factory.getInstance().newEditable(orderSelectedInfo.descripcion) }

        tvOrderInfoTotalPrice.text = orderSelectedInfo.id_orden.price_order.toString()
        tvOrderInfoProductName.text = orderSelectedInfo.id_producto.name
        if(orderSelectedInfo.talla.isNullOrEmpty()){
            tvOrderInfoSelectedSize.text = "N/A"
        }else{
            tvOrderInfoSelectedSize.text = orderSelectedInfo.talla
        }

        if(orderSelectedInfo.color.isNullOrEmpty()){
            tvOrderInfoSelectedColor.text = "N/A"
        }else{
            tvOrderInfoSelectedColor.text = orderSelectedInfo.color
        }

        tvOrderInfoQuantity.text = orderSelectedInfo.cantidad.toString()
        tvOrderInfoUnitPrice.text = orderSelectedInfo.id_producto.precio.toString()

        spOrderStatusChoice.text = Editable.Factory.getInstance().newEditable(orderSelectedInfo.status)

    }


    private fun fillImageOrderView(billOrderImageView: ImageView, orderSelectedInfo: OrderResult){
        billOrderImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Picasso.get()
            .load(orderSelectedInfo.id_orden.image_transaccion.secure_url)
            .fit()
            .centerCrop()
            .into(billOrderImageView)
    }


    private fun navigateBacktoMySalesActivity(user_token: String, user_id: String,  user_rol:String){
        val intent = Intent(this, MySalesActivity::class.java)
        intent.putExtra("USER_TOKEN", user_token)
        intent.putExtra("USER_ID", user_id)
        intent.putExtra("USER_ROL", user_rol)
        startActivity(intent)
    }


    private fun initUI() {
        btnBacktoSalesActivity = findViewById(R.id.btnBacktoSalesActivity)
        btnUpdateUserOrderData = findViewById(R.id.btnUpdateUserOrderData)

        bottom_nav_order_info = findViewById(R.id.bottom_nav_order_info)

        ivBillOrderImage = findViewById(R.id.ivBillOrderImage)

        tvOrderInfoDate = findViewById(R.id.tvOrderInfoDate)
        tvOrderInfoFirstName = findViewById(R.id.tvOrderInfoFirstName)
        tvOrderInfoLastName = findViewById(R.id.tvOrderInfoLastName)
        tvOrderInfoEmail = findViewById(R.id.tvOrderInfoEmail)
        tvOrderInfoAddress = findViewById(R.id.tvOrderInfoAddress)
        tvOrderInfoCellPhone = findViewById(R.id.tvOrderInfoCellPhone)
        tvOrderInfoDescription = findViewById(R.id.tvOrderInfoDescription)
        tvOrderInfoTotalPrice = findViewById(R.id.tvOrderInfoTotalPrice)
        tvOrderInfoProductName = findViewById(R.id.tvOrderInfoProductName)
        tvOrderInfoSelectedSize = findViewById(R.id.tvOrderInfoSelectedSize)
        tvOrderInfoSelectedColor = findViewById(R.id.tvOrderInfoSelectedColor)
        tvOrderInfoQuantity = findViewById(R.id.tvOrderInfoQuantity)
        tvOrderInfoUnitPrice = findViewById(R.id.tvOrderInfoUnitPrice)

        etStatusDescription = findViewById(R.id.etStatusDescription)

        spOrderStatusChoice = findViewById(R.id.spOrderStatusChoice)
        loadStatusSpinner(spOrderStatusChoice)
    }

    private fun loadStatusSpinner(spinner: AutoCompleteTextView){
        statusList = arrayListOf("Pendiente","Rechazado","En entrega","Finalizado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        spinner.setAdapter(adapter)
    }



    private fun uriToFile(uri: Uri): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex: Int? = cursor?.getColumnIndex(filePathColumn[0])
        val filePath: String? = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return filePath?.let { File(it) }
    }

}

