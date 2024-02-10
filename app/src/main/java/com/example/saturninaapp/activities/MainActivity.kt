package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.saturnina.saturninaapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var btnCreateAccount: AppCompatButton
    private lateinit var btnLogIn: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()

        btnCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        btnLogIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }//on create

    private fun initUI(){
        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        btnLogIn = findViewById(R.id.btnLogIn)
    }

}