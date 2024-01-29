package com.example.saturninaapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.models.LoginCredentials
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    private lateinit var etEmailLogin: TextInputEditText
    private lateinit var etPasswordLogin: TextInputEditText
    private lateinit var btnIniciarSesionLogin: Button
    private lateinit var tvRegistrate: Button
    private lateinit var tvRecoverPassword: Button


    private var fileKey: String = "user_data"

    private val MIN_LENGTH_PASSWORD = 9
    private val MAX_LENGTH_PASSWORD = 18

    private var LoginTextWatcher = object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val email: String = etEmailLogin.text.toString()
            val password: String = etPasswordLogin.text.toString()

            disableCLicOnLogin(email,password)


            validEmailText(email)
            validatePasswordLength(password)
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initUI()
        //clearCart(fileKey)

        etEmailLogin.addTextChangedListener(LoginTextWatcher)
        etPasswordLogin.addTextChangedListener(LoginTextWatcher)



        tvRegistrate.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvRecoverPassword.setOnClickListener {
            val intent = Intent(this, RecoverActivity::class.java)
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
                        val error = retrofitPost.errorBody()?.string()
                        val errorBody = error?.let { JSONObject(it) }
                        val detail = errorBody?.opt("detail")
                        var msg = ""

                        when(detail){
                            is JSONObject->{
                                msg = detail.getString("msg")
                            }

                            is JSONArray ->{
                                val firstError = detail.getJSONObject(0)
                                msg = "El correo ingresado no es válido"
                            }
                        }
                        Toast.makeText (this@LoginActivity, msg, Toast.LENGTH_LONG).show()
//                        val detail = errorBody?.getJSONObject("detail")
//                        val msg = detail?.getString("msg")
//                        Toast.makeText (this@LoginActivity, msg, Toast.LENGTH_LONG).show()
//                        Log.e("Error al Logearse: ","${retrofitPost.code()} -- ${retrofitPost.errorBody()?.string()}")
//                        var msg = retrofitPost.errorBody()?.string()
//                        println("MENSAJE JSON: " + msg)
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

        tvRecoverPassword = findViewById(R.id.tvRecoverPassword)
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

    private fun disableCLicOnLogin(email: String, password: String) {
        btnIniciarSesionLogin.isEnabled = ( !email.isNullOrEmpty() ) && ( !password.isNullOrEmpty() )

        when(btnIniciarSesionLogin.isEnabled){
            true->{
                //btnIniciarSesionLogin.setBackgroundColor( resources.getColor(R.color.black) )
                btnIniciarSesionLogin.background = resources.getDrawable(R.drawable.login_register_options_style)
            }

            false->{
                //btnIniciarSesionLogin.setBackgroundColor( resources.getColor(R.color.g_gray500) )
                btnIniciarSesionLogin.background = resources.getDrawable(R.drawable.disabled_buttons_style)
            }
        }

    }


    private fun validEmailText(email: String) {
        var disable = true

        if(!email.contains("@") ||  !email.contains(".")){
            etEmailLogin.error = "El correo debe contener almenos un (.) y debe contener (@)"
            disable = false

            btnIniciarSesionLogin.isClickable = disable
            //btnIniciarSesionLogin.setBackgroundColor( resources.getColor(R.color.g_gray500) )
            btnIniciarSesionLogin.background = resources.getDrawable(R.drawable.disabled_buttons_style)
        }else{
            btnIniciarSesionLogin.isClickable = disable
            //btnIniciarSesionLogin.setBackgroundColor( resources.getColor(R.color.black) )
            btnIniciarSesionLogin.background = resources.getDrawable(R.drawable.login_register_options_style)
        }
    }


    private fun validatePasswordLength( password: String ){


        var disable = true

        if(password.length < MIN_LENGTH_PASSWORD || password.length > MAX_LENGTH_PASSWORD){
            etPasswordLogin.error = "La contraseña debe tener una longitud entre $MIN_LENGTH_PASSWORD y $MAX_LENGTH_PASSWORD caracteres"
            //showToast("El nombre debe tener una longitud mayor a $MIN_LENGTH_PASSWORD caracteres")
            disable = false
            btnIniciarSesionLogin.isClickable = disable
            //btnIniciarSesionLogin.setBackgroundColor( resources.getColor(R.color.g_gray500) )
            btnIniciarSesionLogin.background = resources.getDrawable(R.drawable.disabled_buttons_style)
        }else{
            btnIniciarSesionLogin.isClickable = disable
            //btnIniciarSesionLogin.setBackgroundColor( resources.getColor(R.color.black) )
            btnIniciarSesionLogin.background = resources.getDrawable(R.drawable.login_register_options_style)

        }


    }

/*
    private fun hidePassword(){
        etPasswordLogin.transformationMethod = PasswordTransformationMethod.getInstance()

    }

    private fun showPassword(){
        etPasswordLogin.transformationMethod = HideReturnsTransformationMethod.getInstance()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun clearCart(key: String){
        val sharedPreferences: SharedPreferences = getSharedPreferences(key, MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
*/
}



/*



 */


