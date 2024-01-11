package com.example.saturninaapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.models.UpdateUserProfilePut
import com.example.saturninaapp.util.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {

    private lateinit var etNameProfile: EditText
    private lateinit var etLastProfile: EditText
    private lateinit var etEmailProfile: EditText
    private lateinit var etNumberProfile: EditText
    private lateinit var tvPasswordProfileOptions: TextView
    //Buttons
    private lateinit var btnSaveProfile: AppCompatButton
    private lateinit var btnRegresarDash: AppCompatButton

    //SYMBOLIC CONSTANTS
    private val MIN_LENGTH_PROFILENAME = 3
    private val MAX_LENGTH_PROFILENAME = 10

    private val MIN_LENGTH_PROFILECELLPHONE = 10
    private val MAX_LENGTH_PROFILECELLPHONE = 10



    //WATCHER
    var profileTextWatcher = object:TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val name: String = etNameProfile.text.toString()
            val lastName: String = etLastProfile.text.toString()
            val email: String = etEmailProfile.text.toString()
            val number: String = etNumberProfile.text.toString()

            disableClickSave(name, lastName, email, number)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initUI()

        val user_token = intent.extras?.getString("USER_TOKEN_PROFILE")
        var user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")
        val bearer_token = "Bearer "+user_token
        var userProfile: UpdateUserProfilePut
        println("Mi Token = ${bearer_token}")

        CoroutineScope(Dispatchers.IO).launch {
            val retrofitGetProfile = RetrofitHelper.consumeAPI.getUserProfile(bearer_token)
            if(retrofitGetProfile.isSuccessful){
                val userResponseProfile = retrofitGetProfile.body()
                withContext(Dispatchers.Main) {
                    Log.d("Perfil Obtenido exitosamente", "${userResponseProfile?.detail?.token} ${userResponseProfile?.detail?.nombre} ${userResponseProfile?.detail?.apellido}")
                    println("user_id = ${user_id}")

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
                    try{
                        Log.e("Error al cargar el perfil: ","${retrofitGetProfile.code()} -- ${retrofitGetProfile.errorBody()?.string()}")
                        val msg = retrofitGetProfile.errorBody()?.charStream()?.readText()
                        println("MENSAJE JSON: " + msg)
                    }catch (e: Exception){
                        println("no se pudo obtener el mensaje de error de la api")
                    }

                }
            }
        }




        etNameProfile.addTextChangedListener(profileTextWatcher)
        etLastProfile.addTextChangedListener(profileTextWatcher)
        etEmailProfile.addTextChangedListener(profileTextWatcher)
        etNumberProfile.addTextChangedListener(profileTextWatcher)

        etNameProfile.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                validateUserProfileInputs()
            }

        }

        etLastProfile.setOnFocusChangeListener {_, hasFocus ->
            if(!hasFocus){
                validateUserProfileInputs()
            }

        }

        etNumberProfile.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                validateUserProfileInputs()
            }

        }


        btnSaveProfile.setOnClickListener {
            userProfile = getUserProfileFromUI()
            CoroutineScope(Dispatchers.IO).launch {

                val retrofitUpdateProfile = RetrofitHelper.consumeAPI.updateUserProfile(bearer_token, user_id!!, userProfile)
                println("AQUIIIIIIII")

                if( retrofitUpdateProfile.isSuccessful ){
                    runOnUiThread {
                        val userResponseUpdateProfile = retrofitUpdateProfile.body()
                        val msg = userResponseUpdateProfile?.getAsJsonObject("detail")?.get("msg")?.asString
                        println("userr message $msg")
                        Log.d("Usuario Actualizado", "Se actualizó el usuario ${userProfile.nombre} con id ${user_id}\n ${retrofitUpdateProfile.body().toString()}")
                        paintUserNewData(userProfile)
                    }

                }else{
                    runOnUiThread {
                        Log.e("Error al actualizar el perfil: ","${retrofitUpdateProfile.code()} -- ${retrofitUpdateProfile.errorBody()?.string()}")
                    }
                }
            }
        }//save listener

        btnRegresarDash.setOnClickListener {
            val intent = Intent(applicationContext, DashboardActivity::class.java)
            intent.putExtra("USER_ID", user_id)
            intent.putExtra("USER_TOKEN", user_token)
            intent.putExtra("USER_ROL", user_rol)
            startActivity(intent)

        }


    }//ON CREATE


    private fun initUI() {
        etNameProfile = findViewById(R.id.etNameProfile)
        etLastProfile = findViewById(R.id.etLastProfile)
        etEmailProfile = findViewById(R.id.etEmailProfile)
        etNumberProfile = findViewById(R.id.etNumberProfile)
        tvPasswordProfileOptions = findViewById(R.id.tvPasswordProfileOptions)

        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        btnRegresarDash = findViewById(R.id.btnRegresarLogin)


    }


    private fun disableClickSave(name: String, lastName:String, email:String, number: String) {
        btnSaveProfile.isClickable = ( !name.isNullOrEmpty() ) && ( !lastName.isNullOrEmpty() ) &&
                ( !email.isNullOrEmpty() ) && ( !number.isNullOrEmpty() )

        when(btnSaveProfile.isClickable){
            true->{
                btnSaveProfile.setBackgroundColor( resources.getColor(R.color.blue_button) )
            }
            false->{
                btnSaveProfile.setBackgroundColor( resources.getColor(R.color.g_gray500) )
            }
        }
    }


    private fun validateUserProfileInputs(){
        val name: String = etNameProfile.text.toString()
        val lastName: String = etLastProfile.text.toString()
        val number: String = etNumberProfile.text.toString()

        var disable = false


        if(name.length  !in MIN_LENGTH_PROFILENAME .. MAX_LENGTH_PROFILENAME ){
            showToast("El nombre debe tener una longitud entre $MIN_LENGTH_PROFILENAME y $MAX_LENGTH_PROFILENAME caracteres")
            disable = true
        }


        if( lastName.length !in MIN_LENGTH_PROFILENAME ..  MAX_LENGTH_PROFILENAME){
            showToast("El apellido debe tener una longitud entre $MIN_LENGTH_PROFILENAME y $MAX_LENGTH_PROFILENAME caracteres")
            disable = true
        }


        if( number.length !in MIN_LENGTH_PROFILECELLPHONE ..  MAX_LENGTH_PROFILECELLPHONE){
            showToast("El teléfono debe tener una longitud entre $MIN_LENGTH_PROFILECELLPHONE  caracteres")
            disable = true
        }

        btnSaveProfile.isEnabled = disable
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun paintUserNewData(updatedUser: UpdateUserProfilePut){
        etNameProfile.text =  Editable.Factory.getInstance().newEditable(updatedUser.nombre)
        etLastProfile.text =  Editable.Factory.getInstance().newEditable(updatedUser.apellido)
        etEmailProfile.text =  Editable.Factory.getInstance().newEditable(updatedUser.email)
        etNumberProfile.text =  Editable.Factory.getInstance().newEditable(updatedUser.telefono)
    }

    private fun getUserProfileFromUI(): UpdateUserProfilePut{
        return UpdateUserProfilePut(
            etLastProfile.text.toString(),
            etEmailProfile.text.toString(),
            etNameProfile.text.toString(),
            etNumberProfile.text.toString() )
    }

}// PROFILE ACTIVITY