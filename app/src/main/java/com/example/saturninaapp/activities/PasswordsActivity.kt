package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.models.User

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

        var user = intent.getSerializableExtra("NAME_LAST_NUMBER") as User


        btnGuardarPass.setOnClickListener {
            user.apply {
                correo  = etEmailPass.text.toString()
                password = etPasswordPass.text.toString()
            }
            var confirmPassword = etConfirmPasswordPass.text.toString()
            showUserCompleted(user, confirmPassword)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }


        //return to registerActivity
        btnRegresarPass.setOnClickListener {
            user.apply {
                correo  = etEmailPass.text.toString()
                password = etPasswordPass.text.toString()
            }
            var confirmPassword = etConfirmPasswordPass.text.toString()
            showUserCompleted(user, confirmPassword)
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

    private fun showUserCompleted(user: User, pass:String){
        println(user.nombre + "\n-----" + user.apellido + "\n-----" + user.telefono +
                "\n-----" + user.correo + "\n-----" + user.password + "\n****" + pass)
    }
}