package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R

class PasswordsActivity : AppCompatActivity() {

    private lateinit var etEmailPass:EditText
    private lateinit var etPasswordPass:EditText
    private lateinit var etConfirmPasswordPass:EditText
    private lateinit var btnGuardarPass: AppCompatButton
    private lateinit var btnRegresarPass: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passwords)
        initUI()

        btnGuardarPass.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        //return to registerActivity
        btnRegresarPass.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


    }//ON CREATE


    private fun initUI() {
        etEmailPass = findViewById(R.id.etEmailPass)
        etPasswordPass = findViewById(R.id.etPasswordPass)
        etConfirmPasswordPass = findViewById(R.id.etConfirmPasswordPass)
        btnGuardarPass = findViewById(R.id.btnGuardarPass)
        btnRegresarPass = findViewById(R.id.btnRegresarPass)
    }
}