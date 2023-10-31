package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initUI()

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


}