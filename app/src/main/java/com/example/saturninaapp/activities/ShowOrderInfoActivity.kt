package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.models.OrderResult
import com.squareup.picasso.Picasso

class ShowOrderInfoActivity : AppCompatActivity() {

    private lateinit var btnBacktoSalesActivity: AppCompatButton
    private lateinit var btnUpdateUserOrderData: AppCompatButton

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

    private var OrderTextWatcher = object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val firstName: String = tvOrderInfoFirstName.text.toString()
            val lastName: String = tvOrderInfoLastName.text.toString()
            val email: String = tvOrderInfoEmail.text.toString()
            val address: String = tvOrderInfoAddress.text.toString()
            val phone: String = tvOrderInfoCellPhone.text.toString()
            val description: String = tvOrderInfoDescription.text.toString()

            disableClicOnUpdateOrder(firstName, lastName, email, address, phone, description)
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_order_info)
        initUI()
        disableClicOnUpdateOrder()

        val user_token = intent.extras?.getString("USER_TOKEN")
        val user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")

        val orderSelectedInfo = intent.getSerializableExtra("ORDER_SELECTED") as OrderResult
        fillViewsWithOrderInfo(orderSelectedInfo)

        tvOrderInfoFirstName.addTextChangedListener(OrderTextWatcher)
        tvOrderInfoLastName.addTextChangedListener(OrderTextWatcher)
        tvOrderInfoEmail.addTextChangedListener(OrderTextWatcher)
        tvOrderInfoAddress.addTextChangedListener(OrderTextWatcher)
        tvOrderInfoCellPhone.addTextChangedListener(OrderTextWatcher)
        tvOrderInfoDescription.addTextChangedListener(OrderTextWatcher)



        btnBacktoSalesActivity.setOnClickListener {
            if (user_id != null && user_token != null && user_rol != null) {
                navigateBacktoMySalesActivity(user_token, user_id, user_rol)
            }
        }

        btnUpdateUserOrderData.setOnClickListener {

        }

    }//ON CREATE


    private fun disableClicOnUpdateOrder(){
        btnUpdateUserOrderData.isClickable = false
        btnUpdateUserOrderData.setBackgroundColor( resources.getColor(R.color.g_gray500) )
    }


    private fun disableClicOnUpdateOrder(firstName: String, lastName:String, email:String,
                address: String, phone: String, description: String){

        btnUpdateUserOrderData.isClickable = ( !firstName.isNullOrEmpty() ) && ( !lastName.isNullOrEmpty() )
                && ( !email.isNullOrEmpty() ) && (!address.isNullOrEmpty())  && ( !phone.isNullOrEmpty() )
                && ( !description.isNullOrEmpty() )

        when(btnUpdateUserOrderData.isClickable){
            true -> {
                btnUpdateUserOrderData.setBackgroundColor( resources.getColor(R.color.blue_button) )
            }

            false -> {
                btnUpdateUserOrderData.setBackgroundColor( resources.getColor(R.color.g_gray500) )
            }
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

        tvOrderInfoTotalPrice.text = orderSelectedInfo.id_orden.price_order.toString()
        tvOrderInfoProductName.text = orderSelectedInfo.id_producto.name
        tvOrderInfoSelectedSize.text = orderSelectedInfo.talla
        tvOrderInfoSelectedColor.text = orderSelectedInfo.color
        tvOrderInfoQuantity.text = orderSelectedInfo.cantidad.toString()
        tvOrderInfoUnitPrice.text = orderSelectedInfo.id_producto.precio.toString()

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
    }





}

