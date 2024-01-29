package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.OrdersAdapter
import com.example.saturninaapp.models.OrderResult
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MySalesActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton

    lateinit var bottom_nav_my_sales: BottomNavigationView


    private lateinit var rvSalesManagement: RecyclerView
    private lateinit var salesOrdersAdapter: OrdersAdapter

    private lateinit var etFilterOrders: EditText

    private val itemSalesOrders = mutableListOf<OrderResult>()

    private val ROL_USER: String = "rol:vuqn7k4vw0m1a3wt7fkb"
    private val ROL_ADMIN: String = "rol:74rvq7jatzo6ac19mc79"
    private var user_token: String = ""
    private var user_id: String = ""
    private var user_rol: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sales)
        initUI()

        user_token = intent.extras?.getString("USER_TOKEN").toString()
        user_id = intent.extras?.getString("USER_ID").toString()
        user_rol = intent.extras?.getString("USER_ROL").toString()
        val bearerToken = "Bearer $user_token"


        etFilterOrders.addTextChangedListener {userFilter ->
            val filteredOrders = itemSalesOrders.filter { order -> order.id_orden.nombre.lowercase().contains( userFilter.toString().lowercase() )  }
            salesOrdersAdapter.updateOrdersWithFilter(filteredOrders)
        }


        println("USER ROL: ${user_rol}")
        when(user_rol){
            ROL_USER -> {
                Log.i("INSIDE ROL USER ", "HERE")
                CoroutineScope(Dispatchers.IO).launch {
                    bringOrdersFromSpecificUser(bearerToken, user_id)
                }
            }

            ROL_ADMIN -> {
                Log.i("INSIDE ROL ADMIN ", "HERE")
                CoroutineScope(Dispatchers.IO).launch(){
                    getOrdersFromAllUsers(bearerToken)
                }
            }

            else -> {
                Log.i("SOMEWHERE ", " BUT NOT HERE")
            }
        }


        btnBack.setOnClickListener {
            val intent = Intent(this, ManagementOptionsActivity::class.java)
            intent.putExtra("USER_TOKEN", user_token)
            intent.putExtra("USER_ID", user_id)
            intent.putExtra("USER_ROL", user_rol)
            startActivity(intent)
        }


        salesOrdersAdapter = OrdersAdapter(itemSalesOrders) { order -> showOrderInformation(order) }
        rvSalesManagement.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvSalesManagement.adapter = salesOrdersAdapter



        bottom_nav_my_sales.setOnNavigationItemSelectedListener{

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


    private fun initUI(){

        bottom_nav_my_sales = findViewById(R.id.bottom_nav_my_sales)

        rvSalesManagement = findViewById(R.id.rvSalesManagement)

        etFilterOrders = findViewById(R.id.etFilterOrders)
        //back to management options
        btnBack = findViewById(R.id.btnBack)
    }


    private fun showOrderInformation(orderSelected: OrderResult){
        val intent = Intent(this, ShowOrderInfoActivity::class.java)
        intent.putExtra("ORDER_SELECTED", orderSelected)
        intent.putExtra("USER_TOKEN", user_token)
        intent.putExtra("USER_ID", user_id)
        intent.putExtra("USER_ROL", user_rol)
        startActivity(intent)
    }


    suspend fun bringOrdersFromSpecificUser(bearerToken: String, userId: String){
        try{
            val retrofitGetAllOrders = RetrofitHelper.consumeAPI.getOrdersForUser(bearerToken, userId)

            if(retrofitGetAllOrders.isSuccessful){
                val listResponse = retrofitGetAllOrders.body()?.detail

                withContext(Dispatchers.Main){
                    if(listResponse != null){
                        for(k in listResponse){
                            for(order in k.result){
                                itemSalesOrders.add(OrderResult(order.cantidad, order.color, order.descripcion, order.fecha, order.id, order.id_orden, order.id_producto, order.status, order.talla))
                            }
                        }
                        salesOrdersAdapter.notifyDataSetChanged()
                    }
                }

            }else{
                val msg = retrofitGetAllOrders.errorBody()?.charStream().toString()
                Log.e("ERROR GETTING ALL ORDERS ", "${retrofitGetAllOrders.code()} --**--  ${retrofitGetAllOrders.errorBody()?.toString()}")
                println("MESSAGE: $msg")
            }

        }catch (e: Exception){
            Log.e("ERROR GETTING ALL ORDERS ", e.message.toString())
        }
    }//ORDERS OF SPECIFIC USER


    suspend fun getOrdersFromAllUsers(bearerToken: String){
        try{
            val retrofitGetAllOrders = RetrofitHelper.consumeAPI.getAllOrders(bearerToken)

            if(retrofitGetAllOrders.isSuccessful){
                val listResponse = retrofitGetAllOrders.body()?.detail

                withContext(Dispatchers.Main){
                    /*
                    listResponse?.forEach { detailOrder ->
                        detailOrder.result.let { orderResults ->
                            for(orderData in orderResults){
                                itemSalesOrders.add( OrderResult(orderData.cantidad, orderData.color, orderData.fecha,
                                orderData.id, orderData.id_orden, orderData.id_producto, orderData.status, orderData.talla) )
                            }
                        }
                    }
                    */
                    if(listResponse != null){
                        for(k in listResponse){
                            for(order in k.result){
                                itemSalesOrders.add(OrderResult(order.cantidad, order.color, order.descripcion, order.fecha, order.id, order.id_orden, order.id_producto, order.status, order.talla))
                            }
                        }
                        salesOrdersAdapter.notifyDataSetChanged()
                    }

                }

            }else{
                //NOT SUCCESSFUL
            }

        }catch (e: Exception){
            Log.e("ERROR GETTING ALL ORDERS ", e.message.toString())
        }
    }//ORDERS OF ALL USERS


}//FIN DE LA CLASE MY SALES ACTIVITY