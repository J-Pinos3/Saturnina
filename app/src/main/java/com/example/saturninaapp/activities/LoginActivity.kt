package com.example.saturninaapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.models.LoginCredentials
import com.example.saturninaapp.util.RetrofitHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var etEmailLogin: EditText
    private lateinit var etPasswordLogin: EditText
    private lateinit var btnIniciarSesionLogin: AppCompatButton
    private lateinit var tvRegistrate: TextView

    private var fileKey: String = "user_data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initUI()
        //clearCart(fileKey)
        tvRegistrate.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnIniciarSesionLogin.setOnClickListener {
            val userCredentials = getUsersCredentials()

            CoroutineScope(Dispatchers.IO).launch {
                val retrofitPost = RetrofitHelper.consumeAPI.loginUser(userCredentials)
                if( retrofitPost.isSuccessful ){
                    runOnUiThread{
                        //Log.d("Login exitoso", retrofitPost.body() )
                        val UserResponseLogine = retrofitPost.body()
                        Log.d("Login exitoso", "${UserResponseLogine?.detail?.token!!} ${UserResponseLogine.detail.nombre} ${UserResponseLogine.detail.id}" )
                        val user_token = UserResponseLogine?.detail?.token!!
                        val user_id = UserResponseLogine.detail.id
                        val user_rol = UserResponseLogine.detail.rol

                        saveIdTokenRoleToFile(fileKey, user_token, user_id, user_rol)
                        val intent = Intent(applicationContext, IntroDashboardNews::class.java)
                        intent.putExtra("USER_TOKEN", user_token)
                        intent.putExtra("USER_ID", user_id)
                        intent.putExtra("USER_ROL", user_rol)
                        startActivity(intent)
                    }
                }else{
                    runOnUiThread{
                        Log.e("Error al Logearse: ","${retrofitPost.code()} -- ${retrofitPost.errorBody()?.string()}")
                        var msg = retrofitPost.errorBody()?.string()
                        println("MENSAJE JSON: " + msg)
                    }

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

    private fun saveIdTokenRoleToFile(key: String, userToken: String, userId: String, userRol: String){
        val sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)

        val editor= sharedPreferences.edit()
        editor.putString("USER-TOKEN", userToken)
        editor.putString("USER-ID", userId)
        editor.putString("USER-ROL", userRol)
        editor.apply()
    }

    private fun clearCart(key: String){
        val sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

}