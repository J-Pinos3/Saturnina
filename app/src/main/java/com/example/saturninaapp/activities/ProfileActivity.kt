package com.example.saturninaapp.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.saturnina.saturninaapp.R
import com.example.saturninaapp.models.UpdateUserProfilePut
import com.example.saturninaapp.util.RetrofitHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {

    lateinit var bottom_nav_profile: BottomNavigationView

    private lateinit var etNameProfile: EditText
    private lateinit var etLastProfile: EditText
    private lateinit var etEmailProfile: EditText
    private lateinit var etNumberProfile: EditText

    //Buttons
    private lateinit var btnSaveProfile: AppCompatButton
    private lateinit var btnRegresarDash: ImageButton

    //SYMBOLIC CONSTANTS
    private val MIN_LENGTH_PROFILENAME = 3
    private val MAX_LENGTH_PROFILENAME = 10

    private val MIN_LENGTH_PROFILELASTNAME = 3
    private val MAX_LENGTH_PROFILELASTNAME = 10

    private val MIN_LENGTH_PROFILECELLPHONE = 10
    private val MAX_LENGTH_PROFILECELLPHONE = 10



    //WATCHER
    var profileTextWatcher = object:TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val name: String = etNameProfile.text.toString().trim()
            val lastName: String = etLastProfile.text.toString().trim()
            val email: String = etEmailProfile.text.toString().trim()
            val number: String = etNumberProfile.text.toString().trim()

            disableClickSave(name, lastName, email, number)

            //validateUserProfileInputs(name, lastName, number,email)
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
                    //Log.d("Perfil Obtenido exitosamente", "${userResponseProfile?.detail?.token} ${userResponseProfile?.detail?.nombre} ${userResponseProfile?.detail?.apellido}")
                    //println("user_id = ${user_id}")

                    etNameProfile.post {
                        etNameProfile.text = Editable.Factory.getInstance().newEditable(userResponseProfile?.detail?.nombre?.trim())
                    }

                    etLastProfile.post {
                        etLastProfile.text = Editable.Factory.getInstance().newEditable(userResponseProfile?.detail?.apellido?.trim())
                    }

                    etEmailProfile.post {
                        etEmailProfile.text = Editable.Factory.getInstance().newEditable(userResponseProfile?.detail?.email?.trim())
                    }

                    etNumberProfile.post {
                        etNumberProfile.text = Editable.Factory.getInstance().newEditable(userResponseProfile?.detail?.telefono?.trim())
                    }
                }
            }else{
                runOnUiThread{
                    try{
                        //Log.e("Error al cargar el perfil: ","${retrofitGetProfile.code()} -- ${retrofitGetProfile.errorBody()?.string()}")
                        val error = retrofitGetProfile.errorBody()?.string()
                        val errorBody = error?.let { JSONObject(it) }
                        val detail = errorBody?.opt("detail")
                        var msg = ""

                        when(detail){
                            is JSONObject->{
                                msg = detail.getString("msg")
                            }

                            is JSONArray ->{
                                val firstError = detail.getJSONObject(0)
                                msg = firstError.getString("msg")
                            }
                        }

                        if(msg == "Token inválido o expirado"){
                            //Toast.makeText(this@ProfileActivity, "Por favor vuelve a iniciar sesión", Toast.LENGTH_LONG).show()
                            //NEW
                            val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog, null)
                            val myDialog = Dialog(this@ProfileActivity)
                            myDialog.setContentView(dialogBinding)
                            myDialog.setCancelable(true)
                            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            myDialog.show()

                            val tvAlertMessage = dialogBinding.findViewById<TextView>(R.id.tvAlertMessage)
                            tvAlertMessage.text = "Por favor vuelve a iniciar sesión"
                            val continueButton = dialogBinding.findViewById<AppCompatButton>(R.id.alertContinue)
                            continueButton.setOnClickListener {
                                myDialog.dismiss()
                                val intent = Intent(applicationContext, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            //END NEW

                        }else{
                            Toast.makeText(this@ProfileActivity, msg, Toast.LENGTH_LONG).show()
                        }
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

        etNameProfile.setOnFocusChangeListener { view, b ->
            if (b)
                validateUserProfileInputs(etNameProfile.text.toString(), etLastProfile.text.toString(), etNumberProfile.text.toString(), etEmailProfile.text.toString())
        }
        etLastProfile.setOnFocusChangeListener { view, b ->
            if (b)
                validateUserProfileInputs(etNameProfile.text.toString(), etLastProfile.text.toString(), etNumberProfile.text.toString(), etEmailProfile.text.toString())
        }
        etEmailProfile.setOnFocusChangeListener { view, b ->
            if (b)
                validateUserProfileInputs(etNameProfile.text.toString(), etLastProfile.text.toString(), etNumberProfile.text.toString(), etEmailProfile.text.toString())
        }
        etNumberProfile.setOnFocusChangeListener { view, b ->
            if (b)
                validateUserProfileInputs(etNameProfile.text.toString(), etLastProfile.text.toString(), etNumberProfile.text.toString(), etEmailProfile.text.toString())
        }


        btnSaveProfile.setOnClickListener {
            userProfile = getUserProfileFromUI()
            CoroutineScope(Dispatchers.IO).launch {

                val retrofitUpdateProfile = RetrofitHelper.consumeAPI.updateUserProfile(bearer_token, user_id!!, userProfile)
                //println("AQUIIIIIIII")

                if( retrofitUpdateProfile.isSuccessful ){
                    runOnUiThread {
                        val userResponseUpdateProfile = retrofitUpdateProfile.body()
                        val msg = userResponseUpdateProfile?.getAsJsonObject("detail")?.get("msg")?.asString
                        //println("userr message $msg")
                        //Toast.makeText(this@ProfileActivity, "Usuario Actualizado", Toast.LENGTH_LONG).show()
                        //NEW
                        val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog, null)
                        val myDialog = Dialog(this@ProfileActivity)
                        myDialog.setContentView(dialogBinding)
                        myDialog.setCancelable(true)
                        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        myDialog.show()

                        val tvAlertMessage = dialogBinding.findViewById<TextView>(R.id.tvAlertMessage)
                        tvAlertMessage.text = "Usuario Actualizado Exitosamente"
                        val continueButton = dialogBinding.findViewById<AppCompatButton>(R.id.alertContinue)
                        continueButton.setOnClickListener {
                            myDialog.dismiss()
                        }
                        //END NEW
                        //Log.d("Usuario Actualizado", "Se actualizó el usuario ${userProfile.nombre} con id ${user_id}\n ${retrofitUpdateProfile.body().toString()}")
                        paintUserNewData(userProfile)
                    }

                }else{
                    runOnUiThread {
                        //Log.e("Error al actualizar el perfil: ","${retrofitUpdateProfile.code()} -- ${retrofitUpdateProfile.errorBody()?.string()}")
                        val error = retrofitUpdateProfile.errorBody()?.string()
                        val errorBody = error?.let { JSONObject(it) }
                        val detail = errorBody?.opt("detail")
                        var msg = ""

                        when(detail){
                            is JSONObject->{
                                msg = detail.getString("msg")
                            }

                            is JSONArray ->{
                                val firstError = detail.getJSONObject(0)
                                msg = firstError.getString("msg")
                            }
                        }

                        if(msg == "Token inválido o expirado"){
                            //Toast.makeText(this@ProfileActivity, "Por favor vuelve a iniciar sesión", Toast.LENGTH_LONG).show()
                            //NEW
                            val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog, null)
                            val myDialog = Dialog(this@ProfileActivity)
                            myDialog.setContentView(dialogBinding)
                            myDialog.setCancelable(true)
                            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            myDialog.show()

                            val tvAlertMessage = dialogBinding.findViewById<TextView>(R.id.tvAlertMessage)
                            tvAlertMessage.text = "Por favor vuelve a iniciar sesión"
                            val continueButton = dialogBinding.findViewById<AppCompatButton>(R.id.alertContinue)
                            continueButton.setOnClickListener {
                                myDialog.dismiss()
                                val intent = Intent(applicationContext, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            //END NEW

                        }else{
                            Toast.makeText(this@ProfileActivity, msg, Toast.LENGTH_LONG).show()
                        }
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

        bottom_nav_profile.setOnNavigationItemSelectedListener {

            when(it.itemId){
                R.id.bottom_nav_home->{
                    val intent = Intent(this, IntroDashboardNews::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.bottom_nav_categories->{
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }

                R.id.bottom_nav_comments->{
                    val intent = Intent(this, GenneralComments::class.java)
                    intent.putExtra("USER_TOKEN", user_token)
                    intent.putExtra("USER_ID", user_id)
                    intent.putExtra("USER_ROL", user_rol)
                    startActivity(intent)
                }
            }

            true
        }


    }//ON CREATE


    private fun initUI() {
        etNameProfile = findViewById(R.id.etNameProfile)
        etLastProfile = findViewById(R.id.etLastProfile)
        etEmailProfile = findViewById(R.id.etEmailProfile)
        etNumberProfile = findViewById(R.id.etNumberProfile)

        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        btnRegresarDash = findViewById(R.id.btnRegresarLogin)


        bottom_nav_profile = findViewById(R.id.bottom_nav_profile)
    }


    private fun disableClickSave(name: String, lastName:String, email:String, number: String) {
        btnSaveProfile.isClickable = ( !name.isNullOrEmpty() ) && ( !lastName.isNullOrEmpty() ) &&
                ( !email.isNullOrEmpty() ) && ( !number.isNullOrEmpty() )

        when(btnSaveProfile.isClickable){
            true->{
                btnSaveProfile.background = resources.getDrawable(R.drawable.login_register_options_style)
            }
            false->{
                btnSaveProfile.background = resources.getDrawable(R.drawable.disabled_buttons_style)
            }
        }
    }



    private fun validateUserProfileInputs(name: String, lastName: String, number: String, email: String){


        var clickable = true


        if(name.length  !in MIN_LENGTH_PROFILENAME .. MAX_LENGTH_PROFILENAME ){
            //Log.e("NAME LENGTH", " --**-- ${name}, ${name.length}")
            etNameProfile.error = "El nombre debe tener una longitud entre $MIN_LENGTH_PROFILENAME y $MAX_LENGTH_PROFILENAME caracteres"
            clickable = false
        }


        if( lastName.length !in MIN_LENGTH_PROFILENAME ..  MAX_LENGTH_PROFILENAME){
            //Log.e("LAST LENGTH", " --**-- ${lastName}, ${lastName.length}")
            etLastProfile.error = "El apellido debe tener una longitud entre $MIN_LENGTH_PROFILENAME y $MAX_LENGTH_PROFILENAME caracteres"
            clickable = false
        }

        if(!email.contains("@") ||  !email.contains(".")){
            etEmailProfile.error = "El correo debe tener @ y al menos un ."
            clickable = false
        }

        if( number.length != MIN_LENGTH_PROFILECELLPHONE){
           // Log.e("NUMBER LENGTH", " --**-- ${number}, ${number.length}")
            etNumberProfile.error = "El teléfono debe tener una longitud de $MIN_LENGTH_PROFILECELLPHONE  caracteres"
            clickable = false
        }

        if(clickable){
            btnSaveProfile.background = resources.getDrawable(R.drawable.login_register_options_style)
        }else{
            btnSaveProfile.background = resources.getDrawable(R.drawable.disabled_buttons_style)
        }

        btnSaveProfile.isClickable = clickable
    }


//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }


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