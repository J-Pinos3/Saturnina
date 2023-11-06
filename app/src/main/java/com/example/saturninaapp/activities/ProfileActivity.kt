package com.example.saturninaapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.util.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var etNameProfile: EditText
    private lateinit var etLastProfile: EditText
    private lateinit var etEmailProfile: EditText
    private lateinit var etNumberProfile: EditText
    private lateinit var tvPasswordProfileOptions: TextView
    //Buttons
    private lateinit var btnSaveLogin: AppCompatButton
    private lateinit var btnRegresarLogin: AppCompatButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initUI()
        val user_token = intent.extras?.getString("USER_TOKEN_PROFILE")
        val bearer_token = "Bearer "+user_token
        println("Mi Token = ${bearer_token}")

        CoroutineScope(Dispatchers.IO).launch {
            val retrofitGetProfile = RetrofitHelper.consumeAPI.getUserProfile(bearer_token)
            if(retrofitGetProfile.isSuccessful){
                val userResponseProfile = retrofitGetProfile.body()
                withContext(Dispatchers.Main) {
                    //Log.d("Perfil Obtenido exitosamente", "${userResponseProfile?.detail?.token} ${userResponseProfile?.detail?.nombre} ${userResponseProfile?.detail?.apellido}")
                    etNameProfile.post {
                        etNameProfile.text = Editable.Factory.getInstance().newEditable(userResponseProfile?.detail?.nombre)
                    }

                    etLastProfile.post {
                        etLastProfile.text = Editable.Factory.getInstance().newEditable(userResponseProfile?.detail?.apellido)
                    }

                    etEmailProfile.post {
                        etEmailProfile.text = Editable.Factory.getInstance().newEditable(userResponseProfile?.detail?.email)
                    }

                    etNumberProfile.post {
                        etNumberProfile.text = Editable.Factory.getInstance().newEditable(userResponseProfile?.detail?.telefono)
                    }
                }
            }else{
                runOnUiThread{
                    Log.e("Error al cargar el perfil: ","${retrofitGetProfile.code()} -- ${retrofitGetProfile.errorBody()?.string()}")
                    val msg = retrofitGetProfile.errorBody()?.string()
                    println("MENSAJE JSON: " + msg)
                }
            }
        }

        /*
        btnSaveLogin.isClickable = enableSaveClick()
        if( btnSaveLogin.isClickable ){//si no es clickable, ingrese datos
            btnSaveLogin.setOnClickListener {

            }
        }
        */



    }//ON CREATE


    private fun initUI() {
        etNameProfile = findViewById(R.id.etNameProfile)
        etLastProfile = findViewById(R.id.etLastProfile)
        etEmailProfile = findViewById(R.id.etEmailProfile)
        etNumberProfile = findViewById(R.id.etNumberProfile)
        tvPasswordProfileOptions = findViewById(R.id.tvPasswordProfileOptions)

        btnSaveLogin = findViewById(R.id.btnSaveLogin)
        btnRegresarLogin = findViewById(R.id.btnRegresarLogin)


    }

    private fun enableSaveClick(): Boolean{
        var clickable = false

        if( !etNameProfile.text.toString().isNullOrEmpty() && !etLastProfile.text.toString().isNullOrEmpty() &&
            !etEmailProfile.text.toString().isNullOrEmpty() && !etNumberProfile.text.toString().isNullOrEmpty() ) {

            clickable = true
        }

        return clickable
    }

}// PROFILE ACTIVITY