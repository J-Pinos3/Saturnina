package com.example.saturninaapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.android.material.navigation.NavigationView

class MySalesActivity : AppCompatActivity() {

    private lateinit var btnBack: AppCompatButton

    private lateinit var rvSalesManagement: RecyclerView
    private lateinit var salesOrders: OrdersAdapter

    private val itemSalesOrders = mutableListOf<OrderResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sales)
        initUI()

        val user_token = intent.extras?.getString("USER_TOKEN")
        val user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")





    }


    private fun initUI(){
        rvSalesManagement = findViewById(R.id.rvSalesManagement)

        //back to management options
        btnBack = findViewById(R.id.btnBack)

        salesOrders = OrdersAdapter(mutableListOf<OrderResult>())
    }


}//FIN DE LA CLASE MY SALES ACTIVITY