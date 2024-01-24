package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.models.User
import com.example.saturninaapp.util.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class PasswordsActivity : AppCompatActivity() {

    private lateinit var etEmailPass:EditText
    private lateinit var etPasswordPass:EditText
    private lateinit var etConfirmPasswordPass:EditText
    private lateinit var btnGuardarPass: AppCompatButton
    private lateinit var btnRegresarPass: AppCompatButton

    private lateinit var cbFirstPassword: CheckBox
    private lateinit var cbSecondPassword: CheckBox

    private val MIN_LENGTH_PASSWORD = 9
    private val MAX_LENGTH_PASSWORD = 18

    private var PasswordsTextWatcher = object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val email = etEmailPass.text.toString()
            val pass1: String = etPasswordPass.text.toString()
            val pass2: String = etConfirmPasswordPass.text.toString()

            disableClicOnRegister(email, pass1, pass2)

            validateInputsEmailPassword(email, pass1, pass2)
        }

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passwords)
        initUI()

        var user = intent.getSerializableExtra("NAME_LAST_NUMBER") as User


        etEmailPass.addTextChangedListener(PasswordsTextWatcher)
        etPasswordPass.addTextChangedListener(PasswordsTextWatcher)
        etConfirmPasswordPass.addTextChangedListener(PasswordsTextWatcher)


        cbFirstPassword.setOnCheckedChangeListener { _, b ->
            if(b)
                showFirstPassword()
            else
                hideFirstPassword()
        }

        cbSecondPassword.setOnCheckedChangeListener { _, b ->
            if(b)
                showSecondPassword()
            else
                hideSecondPassword()
        }

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
                        val jsonResponse = retrofitPost.body().toString()
                        val jsonObject = JSONObject(jsonResponse)
                        val detailObject = jsonObject.getJSONObject("detail")
                        val msg = detailObject.getString("msg")
                        Toast.makeText(this@PasswordsActivity, msg, Toast.LENGTH_LONG).show()

                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }else{
                    Log.e("Error: ","${retrofitPost.code()} -- ${retrofitPost.errorBody()?.string()}")
                }

            }



        }


        //return to registerActivity
        btnRegresarPass.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


    }//ON CREATE

    private fun disableClicOnRegister(email: String, pass1: String, pass2: String) {
        btnGuardarPass.isEnabled = (email.isNotEmpty()) && (pass1.isNotEmpty()) && (pass2.isNotEmpty())


        when( btnGuardarPass.isEnabled ){
            true->{
                btnGuardarPass.setBackgroundColor( resources.getColor(R.color.blue_button) )
            }

            false->{
                btnGuardarPass.setBackgroundColor( resources.getColor(R.color.g_gray500) )
            }
        }

    }


    private fun validateInputsEmailPassword(email:String, pass1: String, pass2: String) {
        var clickable = true

        if(pass1.length !in MIN_LENGTH_PASSWORD .. MAX_LENGTH_PASSWORD){
            etPasswordPass.error = "La contraseña debe tener una longitud de $MIN_LENGTH_PASSWORD a $MAX_LENGTH_PASSWORD caracteres "
            clickable = false
        }

        if(pass2.length !in MIN_LENGTH_PASSWORD .. MAX_LENGTH_PASSWORD){
            etConfirmPasswordPass.error = "La contraseña debe tener una longitud de $MIN_LENGTH_PASSWORD a $MAX_LENGTH_PASSWORD caracteres "
            clickable = false
        }

        if( !email.contains("@") || !email.contains(".") ){
            etEmailPass.error = "El correo debe contener almenos un (.) y debe contener (@)"
            clickable = false
        }

        if( pass1 != pass2 ){
            etConfirmPasswordPass.error = "Las contraseñas no coinciden"
            clickable = false
        }

        if( !pass1.matches(".*[A-Z].*".toRegex()) ){
            etPasswordPass.error = "La contraseña debe contener almenos una letra mayúscula"
            clickable = false
        }

        if( !pass1.matches(".*[+/x*-@()_!].*".toRegex()) ){
            etPasswordPass.error = "La contraseña debe contener almenos un caracter especial"
            clickable = false
        }

        if(clickable){
            btnGuardarPass.setBackgroundColor( resources.getColor(R.color.blue_button) )
        }else{
            btnGuardarPass.setBackgroundColor( resources.getColor(R.color.g_gray500) )
        }
        btnGuardarPass.isClickable = clickable
    }


    private fun initUI() {
        etEmailPass = findViewById(R.id.etEmailPass)
        etPasswordPass = findViewById(R.id.etPasswordPass)
        etConfirmPasswordPass = findViewById(R.id.etConfirmPasswordPass)
        btnGuardarPass = findViewById(R.id.btnGuardarPass)
        btnRegresarPass = findViewById(R.id.btnRegresarPass)

        cbFirstPassword = findViewById(R.id.cbFirstPassword)
        cbSecondPassword = findViewById(R.id.cbSecondPassword)
    }

    private fun hideFirstPassword(){
        etPasswordPass.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    private fun showFirstPassword(){
        etConfirmPasswordPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
    }

    private fun hideSecondPassword(){
        etConfirmPasswordPass.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    private fun showSecondPassword(){
        etConfirmPasswordPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
    }

    private fun showUserCompleted(user: User, pass:String){
        println(user.nombre + "\n-----" + user.apellido + "\n-----" + user.telefono +
                "\n-----" + user.correo + "\n-----" + user.password + "\n****" + pass)
    }
}