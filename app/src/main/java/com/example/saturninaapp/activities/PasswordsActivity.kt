package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.models.User
import com.example.saturninaapp.util.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

            //ejecutamos la petición en otro hilo
            CoroutineScope(Dispatchers.IO).launch {
                val retrofitPost = RetrofitHelper.consumeAPI.createUser(user)
                if( retrofitPost.isSuccessful ){
                    runOnUiThread {
                        Log.d("Llamada exitosa", "${retrofitPost.body()?.correo}")
                    }
                }else{
                    Log.e("Error: ","${retrofitPost.code()} -- ${retrofitPost.message()}")
                }

            }

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

    private fun showUserCompleted(user: User, pass:String){
        println(user.nombre + "\n-----" + user.apellido + "\n-----" + user.telefono +
                "\n-----" + user.correo + "\n-----" + user.password + "\n****" + pass)
    }
}