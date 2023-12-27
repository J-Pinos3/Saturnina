package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.ItemClothesAdapter
import com.example.saturninaapp.adapters.OrdersAdapter
import com.example.saturninaapp.models.DetailProduct
import com.example.saturninaapp.models.OrderResult
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MySalesActivity : AppCompatActivity() {

    private lateinit var btnBack: AppCompatButton

    private lateinit var rvSalesManagement: RecyclerView
    private lateinit var salesOrdersAdapter: OrdersAdapter

    private val itemSalesOrders = mutableListOf<OrderResult>()

    private val ROL_USER: String = "rol:vuqn7k4vw0m1a3wt7fkb"
    private val ROL_ADMIN: String = "rol:74rvq7jatzo6ac19mc79"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sales)
        initUI()

        val user_token = intent.extras?.getString("USER_TOKEN")
        val user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")
        val bearerToken = "Bearer $user_token"


        salesOrdersAdapter = OrdersAdapter(itemSalesOrders) { order -> showOrderInformation(order) }
        rvSalesManagement.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvSalesManagement.adapter = salesOrdersAdapter


        when(user_rol){
            ROL_USER -> {
                CoroutineScope(Dispatchers.IO).launch {
                    if (user_id != null) {
                        bringOrdersFromSpecificUser(bearerToken, user_id)
                    }
                }
            }

            ROL_ADMIN -> {
                CoroutineScope(Dispatchers.IO).launch(){
                    getOrdersFromAllUsers(bearerToken)
                }
            }

            else -> {
                println("Esto no debería pasar :(")
            }
        }



    }//ON CREATE


    private fun initUI(){
        rvSalesManagement = findViewById(R.id.rvSalesManagement)

        //back to management options
        btnBack = findViewById(R.id.btnBack)
    }


    private fun showOrderInformation(orderSelected: OrderResult){
        val intent = Intent(this, ShowOrderInfoActivity::class.java)
        intent.putExtra("ORDER_SELECTED", orderSelected)
        startActivity(intent)
    }


    suspend fun bringOrdersFromSpecificUser(bearerToken: String, userId: String){
        try{
            val retrofitGetAllOrders = RetrofitHelper.consumeAPI.getOrdersForUser(bearerToken, userId)

            if(retrofitGetAllOrders.isSuccessful){
                val listResponse = retrofitGetAllOrders.body()?.detail

                withContext(Dispatchers.Main){
                    listResponse?.forEach { detailOrder ->
                        detailOrder.result.let { orderResults ->
                            for(orderData in orderResults){
                                itemSalesOrders.add( OrderResult(orderData.cantidad, orderData.color, orderData.fecha,
                                    orderData.id, orderData.id_orden, orderData.id_producto, orderData.status, orderData.talla) )
                            }
                        }
                    }
                }

            }else{
                //NOT SUCCESSFUL
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
                    listResponse?.forEach { detailOrder ->
                        detailOrder.result.let { orderResults ->
                            for(orderData in orderResults){
                                itemSalesOrders.add( OrderResult(orderData.cantidad, orderData.color, orderData.fecha,
                                orderData.id, orderData.id_orden, orderData.id_producto, orderData.status, orderData.talla) )
                            }
                        }
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