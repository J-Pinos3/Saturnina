package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.models.User
import java.io.Serializable

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNameRegister: EditText
    private lateinit var etLastNameRegister: EditText
    private lateinit var etNumberRegister: EditText
    private lateinit var btnContinuarRegister: AppCompatButton
    private lateinit var btnRegresarRegister: AppCompatButton

    private val MIN_LENGTH_NAME = 3
    private val MAX_LENGTH_NAME = 10

    private val MIN_LENGTH_CELLPHONE = 10
    private val MAX_LENGTH_CELLPHONE = 10

    private var RegisterTextWatcher = object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val name: String = etNameRegister.text.toString()
            val lastName: String = etLastNameRegister.text.toString()
            val number: String = etNumberRegister.text.toString()

            disableClicOnContinue(name, lastName, number,)

            validateInputsLenght(name, lastName, number,)
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initUI()

        etNameRegister.addTextChangedListener(RegisterTextWatcher)
        etLastNameRegister.addTextChangedListener(RegisterTextWatcher)
        etNumberRegister.addTextChangedListener(RegisterTextWatcher)



        btnContinuarRegister.setOnClickListener {
            val intent = Intent(this, PasswordsActivity::class.java)
            val user = getUsersNameLastNumber()
            println("REGISTERACTIVITY: ${user.nombre} ${user.apellido} ${user.telefono}")
            intent.putExtra("NAME_LAST_NUMBER", user )
            startActivity(intent)
        }

        //return to mainActivity
        btnRegresarRegister.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }//ON CREATE


    private fun initUI() {
        etNameRegister = findViewById(R.id.etNameRegister)
        etLastNameRegister = findViewById(R.id.etLastNameRegister)
        etNumberRegister = findViewById(R.id.etNumberRegister)
        btnContinuarRegister = findViewById(R.id.btnContinuarRegister)
        btnRegresarRegister = findViewById(R.id.btnRegresarRegister)
    }

    private fun getUsersNameLastNumber(): User{
        return User(etNameRegister.text.toString(),
            etLastNameRegister.text.toString(),
            etNumberRegister.text.toString()
            )
    }


    private fun disableClicOnContinue(name: String, lastName: String, number: String) {
        btnContinuarRegister.isClickable = ( name.isNotEmpty() ) && ( lastName.isNotEmpty() ) && ( number.isNotEmpty() )

        when(btnContinuarRegister.isEnabled){
            true -> {
                btnContinuarRegister.background = resources.getDrawable(R.drawable.login_register_options_style)
            }

            false -> {
                btnContinuarRegister.background = resources.getDrawable(R.drawable.disabled_buttons_style)
            }
        }

    }


    private fun validateInputsLenght(name: String, lastName: String, number: String) {
        var clickable = true

        if( name.length !in MIN_LENGTH_NAME .. MAX_LENGTH_NAME ){
            etNameRegister.error = "El nombre debe tener una longitud entre $MAX_LENGTH_NAME y $MAX_LENGTH_NAME caracteres"
            clickable = false
        }


        if( lastName.length !in MIN_LENGTH_NAME .. MAX_LENGTH_NAME ){
            etLastNameRegister.error = "El apellido debe tener una longitud entre $MAX_LENGTH_NAME y $MAX_LENGTH_NAME caracteres"
            clickable = false
        }


        if( number.length != MIN_LENGTH_CELLPHONE ){
            etNumberRegister.error = "El teléfono debe tener $MIN_LENGTH_CELLPHONE dígitos"
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