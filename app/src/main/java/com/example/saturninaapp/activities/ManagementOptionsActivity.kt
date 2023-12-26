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

        btnSalesManagementOptions.setOnClickListener {
            val intent = Intent(applicationContext, MySalesActivity::class.java)
            startActivity(intent)
        }


        btnBackManagementOptions.setOnClickListener {
            val intent = Intent(applicationContext, IntroDashboardNews::class.java)
            startActivity(intent)
        }
    }//ON CREATE

    private fun initUI() {
        btnSalesManagementOptions = findViewById(R.id.btnSalesManagementOptions)
        btnBackManagementOptions = findViewById(R.id.btnBackManagementOptions)
    }


}