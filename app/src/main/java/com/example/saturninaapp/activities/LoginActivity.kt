package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.models.LoginCredentials
import com.example.saturninaapp.models.UserResponseLogin
import com.example.saturninaapp.util.RetrofitHelper
import com.example.saturninaapp.util.UtilClasses
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmailLogin: EditText
    private lateinit var etPasswordLogin: EditText
    private lateinit var btnIniciarSesionLogin: AppCompatButton
    private lateinit var tvRegistrate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initUI()

        tvRegistrate.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnIniciarSesionLogin.setOnClickListener {
            val userCredentials = getUsersCredentials()
            var navegar = false

            CoroutineScope(Dispatchers.IO).launch {
                val retrofitPost = RetrofitHelper.consumeAPI.loginUser(userCredentials)
                if( retrofitPost.isSuccessful ){
                    runOnUiThread{
                        //Log.d("Login exitoso", retrofitPost.body() )
                        val UserResponseLogine = retrofitPost.body()
                        Log.d("Login exitoso", "${UserResponseLogine?.detail?.token!!} ${UserResponseLogine.detail.nombre}" )

                    }
                    val intent = Intent(applicationContext, DashboardActivity::class.java)
                    startActivity(intent)

                }else{
                    Log.e("Error al Logearse: ","${retrofitPost.code()} -- ${retrofitPost.errorBody()?.string()}")
                }
            }


        }

    }//on create


    private fun initUI(){
        etEmailLogin = findViewById(R.id.etEmailLogin)
        etPasswordLogin = findViewById(R.id.etPasswordLogin)
        btnIniciarSesionLogin = findViewById(R.id.btnIniciarSesionLogin)
        tvRegistrate = findViewById(R.id.tvRegistrate)
    }

    private fun getUsersCredentials(): LoginCredentials{
        return LoginCredentials(
            etEmailLogin.text.toString(),
            etPasswordLogin.text.toString() )
    }

}