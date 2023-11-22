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
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.models.UpdateUserProfilePut
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
    private lateinit var btnSaveProfile: AppCompatButton
    private lateinit var btnRegresarDash: AppCompatButton

    //WATCHER
    var profileTextWatcher = object:TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val name: String = etNameProfile.text.toString()
            val lastName: String = etLastProfile.text.toString()
            val email: String = etEmailProfile.text.toString()
            val number: String = etNumberProfile.text.toString()

            disableClickSave(name, lastName, email, number)
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initUI()

        val user_token = intent.extras?.getString("USER_TOKEN_PROFILE")
        val bearer_token = "Bearer "+user_token
        var user_id: String = ""
        var userProfile: UpdateUserProfilePut
        println("Mi Token = ${bearer_token}")

        CoroutineScope(Dispatchers.IO).launch {
            val retrofitGetProfile = RetrofitHelper.consumeAPI.getUserProfile(bearer_token)
            if(retrofitGetProfile.isSuccessful){
                val userResponseProfile = retrofitGetProfile.body()
                withContext(Dispatchers.Main) {
                    //Log.d("Perfil Obtenido exitosamente", "${userResponseProfile?.detail?.token} ${userResponseProfile?.detail?.nombre} ${userResponseProfile?.detail?.apellido}")
                    user_id = userResponseProfile?.detail?.id.toString()

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




        etNameProfile.addTextChangedListener(profileTextWatcher)
        etLastProfile.addTextChangedListener(profileTextWatcher)
        etEmailProfile.addTextChangedListener(profileTextWatcher)
        etNumberProfile.addTextChangedListener(profileTextWatcher)



        btnSaveProfile.setOnClickListener {
            userProfile = getUserProfileFromUI()
            CoroutineScope(Dispatchers.IO).launch {

                val retrofitUpdateProfile = RetrofitHelper.consumeAPI.updateUserProfile(bearer_token, user_id, userProfile)
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
                        Log.e("Error al Logearse: ","${retrofitUpdateProfile.code()} -- ${retrofitUpdateProfile.errorBody()?.string()}")
                    }
                }
            }
        }//save listener

        btnRegresarDash.setOnClickListener {
            val intent = Intent(applicationContext, DashboardActivity::class.java)
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