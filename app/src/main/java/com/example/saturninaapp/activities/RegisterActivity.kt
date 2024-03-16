package com.example.saturninaapp.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.inputmethodservice.InputMethodService
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.saturnina.saturninaapp.R
import com.example.saturninaapp.models.User
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNameRegister: TextInputEditText
    private lateinit var etLastNameRegister: TextInputEditText
    private lateinit var etNumberRegister: TextInputEditText
    private lateinit var etEmailPass:TextInputEditText
    private lateinit var etPasswordPass:TextInputEditText
    private lateinit var etConfirmPasswordPass:EditText

    private lateinit var til_email: TextInputLayout
    private lateinit var til_nickname: TextInputLayout
    private lateinit var last_name: TextInputLayout
    private lateinit var til_phone_number: TextInputLayout
    private lateinit var til_password: TextInputLayout
    private lateinit var til_verify_password: TextInputLayout

    private lateinit var constraintRegister: ConstraintLayout


    private lateinit var btnContinuarRegister: AppCompatButton
    private lateinit var btnRegresarRegister: ImageButton

    private val MIN_LENGTH_NAME = 3
    private val MAX_LENGTH_NAME = 10

    private val MIN_LENGTH_CELLPHONE = 10
    private val MAX_LENGTH_CELLPHONE = 10

    private val MIN_LENGTH_PASSWORD = 9
    private val MAX_LENGTH_PASSWORD = 18

    private var RegisterTextWatcher = object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val name: String = etNameRegister.text.toString()
            val lastName: String = etLastNameRegister.text.toString()
            val number: String = etNumberRegister.text.toString()
            val email = etEmailPass.text.toString()
            val pass1: String = etPasswordPass.text.toString()
            val pass2: String = etConfirmPasswordPass.text.toString()

            disableClicOnContinue(name, lastName, number, email, pass1, pass2)

            validateInputsLenght(name, lastName, number, email, pass1, pass2)
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initUI()

        etNameRegister.addTextChangedListener(RegisterTextWatcher)
        etLastNameRegister.addTextChangedListener(RegisterTextWatcher)
        etNumberRegister.addTextChangedListener(RegisterTextWatcher)
        etEmailPass.addTextChangedListener ( RegisterTextWatcher )
        etPasswordPass.addTextChangedListener ( RegisterTextWatcher )
        etConfirmPasswordPass.addTextChangedListener ( RegisterTextWatcher )

        //NEW
        val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog, null)
        val myDialog = Dialog(this@RegisterActivity)
        myDialog.setContentView(dialogBinding)
        myDialog.setCancelable(true)
        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val tvAlertMessage = dialogBinding.findViewById<TextView>(R.id.tvAlertMessage)
        val continueButton = dialogBinding.findViewById<AppCompatButton>(R.id.alertContinue)
        continueButton.setOnClickListener {
            myDialog.dismiss()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }
        //END NEW

        btnContinuarRegister.setOnClickListener {

            val user = getUsersData()
            //println("REGISTERACTIVITY: ${user.nombre} ${user.apellido} ${user.telefono}")

            CoroutineScope(Dispatchers.IO).launch {
                val retrofitPost = RetrofitHelper.consumeAPI.createUser(user)
                if( retrofitPost.isSuccessful ){
                    runOnUiThread {
                        val jsonResponse = retrofitPost.body().toString()
                        val jsonObject = JSONObject(jsonResponse)
                        val detailObject = jsonObject.getJSONObject("detail")
                        val msg = detailObject.getString("msg")
                        //Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_LONG).show()
                        myDialog.show()
                        tvAlertMessage.text = msg

                    }
                }else{
                    //Log.e("Error: ","${retrofitPost.code()} -- ${retrofitPost.errorBody()?.string()}")
                }

            }

        }

        //return to mainActivity
        btnRegresarRegister.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        constraintRegister.setOnClickListener {
            val view: View? = this.currentFocus

            if(view != null){
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }


    }//ON CREATE


    private fun initUI() {
        etNameRegister = findViewById(R.id.etNameRegister)
        etLastNameRegister = findViewById(R.id.etLastNameRegister)
        etNumberRegister = findViewById(R.id.etNumberRegister)
        etEmailPass = findViewById(R.id.etEmailPass)
        etPasswordPass = findViewById(R.id.etPasswordPass)
        etConfirmPasswordPass = findViewById(R.id.etConfirmPasswordPass)

        btnContinuarRegister = findViewById(R.id.btnContinuarRegister)
        btnRegresarRegister = findViewById(R.id.btnRegresarRegister)

        til_email = findViewById(R.id.til_email)
        til_nickname = findViewById(R.id.til_nickname)
        last_name = findViewById(R.id.last_name)
        til_phone_number = findViewById(R.id.til_phone_number)
        til_password = findViewById(R.id.til_password)
        til_verify_password = findViewById(R.id.til_verify_password)

        constraintRegister = findViewById(R.id.constraintRegister)
    }

    private fun getUsersData(): User{
        return User(etNameRegister.text.toString(),
            etLastNameRegister.text.toString(),
            etNumberRegister.text.toString(),
            etEmailPass.text.toString(),
            etPasswordPass.text.toString()
            )
    }


    private fun disableClicOnContinue(name: String, lastName: String, number: String, email: String, pass1: String, pass2: String) {
        btnContinuarRegister.isClickable = ( name.isNotEmpty() ) && ( lastName.isNotEmpty() ) && ( number.isNotEmpty() ) && (email.isNotEmpty()) && (pass1.isNotEmpty()) && (pass2.isNotEmpty())

        when(btnContinuarRegister.isEnabled){
            true -> {
                btnContinuarRegister.background = resources.getDrawable(R.drawable.login_register_options_style)
            }

            false -> {
                btnContinuarRegister.background = resources.getDrawable(R.drawable.disabled_buttons_style)
            }
        }

    }


    private fun validateInputsLenght(name: String, lastName: String,  number: String,  email: String,  pass1: String, pass2: String ) {
        var clickable = true

        if( name.length !in MIN_LENGTH_NAME .. MAX_LENGTH_NAME ){
            etNameRegister.error = "El nombre debe tener una longitud entre $MIN_LENGTH_NAME y $MAX_LENGTH_NAME caracteres"
            clickable = false
        }


        if( lastName.length !in MIN_LENGTH_NAME .. MAX_LENGTH_NAME ){
            etLastNameRegister.error = "El apellido debe tener una longitud entre $MIN_LENGTH_NAME y $MAX_LENGTH_NAME caracteres"
            clickable = false
        }


        if( number.length != MIN_LENGTH_CELLPHONE ){
            etNumberRegister.error = "El teléfono debe tener $MIN_LENGTH_CELLPHONE dígitos"
            clickable = false
        }

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

        if( !pass1.matches(".*[+/*\\\\\\-@()_!].*".toRegex()) ){
            etPasswordPass.error = "La contraseña debe contener almenos un caractér especial"
            clickable = false
        }

        if(clickable){
            btnContinuarRegister.background = resources.getDrawable(R.drawable.login_register_options_style)
        }else{
            btnContinuarRegister.background = resources.getDrawable(R.drawable.disabled_buttons_style)
        }
        btnContinuarRegister.isClickable = clickable

    }



}