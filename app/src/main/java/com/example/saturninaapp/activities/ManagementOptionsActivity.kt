package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R

class ManagementOptionsActivity : AppCompatActivity() {

    private lateinit var btnSalesManagementOptions: AppCompatButton
    private lateinit var btnBackManagementOptions: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_management_options)
        initUI()

        val user_token = intent.extras?.getString("USER_TOKEN")
        val user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")

        btnSalesManagementOptions.setOnClickListener {
            val intent = Intent(applicationContext, MySalesActivity::class.java)
            intent.putExtra("USER_TOKEN", user_token)
            intent.putExtra("USER_ID", user_id)
            intent.putExtra("USER_ROL", user_rol)
            startActivity(intent)
        }


        btnBackManagementOptions.setOnClickListener {
            val intent = Intent(applicationContext, IntroDashboardNews::class.java)
            intent.putExtra("USER_TOKEN", user_token)
            intent.putExtra("USER_ID", user_id)
            intent.putExtra("USER_ROL", user_rol)
            startActivity(intent)
        }
    }//ON CREATE

    private fun initUI() {
        btnSalesManagementOptions = findViewById(R.id.btnSalesManagementOptions)
        btnBackManagementOptions = findViewById(R.id.btnBackManagementOptions)
    }


}