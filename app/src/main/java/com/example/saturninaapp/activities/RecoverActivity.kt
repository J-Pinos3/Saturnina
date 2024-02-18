package com.example.saturninaapp.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.saturnina.saturninaapp.R
import com.example.saturninaapp.models.RecoverPassword
import com.example.saturninaapp.util.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class RecoverActivity : AppCompatActivity() {

    private lateinit var etEmailRecover: EditText
    private lateinit var btnContinueRecover: AppCompatButton
    private lateinit var btnRegresarLOGIN: ImageView


    private var RecoverTextWatcher = object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val email: String = etEmailRecover.text.toString()

            disableClicOnSendRecover(email)
            validEmailText(email)
        }


    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover)

        initUI()

        etEmailRecover.addTextChangedListener(RecoverTextWatcher)

        etEmailRecover.setOnFocusChangeListener { view, b ->
            if(b)
                validEmailText(etEmailRecover.text.toString())
        }

        btnContinueRecover.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                getNewPassword(etEmailRecover.text.toString())
            }
        }

        btnRegresarLOGIN.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

    }//ON CREATE

    private fun initUI() {
        etEmailRecover = findViewById(R.id.etEmailRecover)
        btnContinueRecover = findViewById(R.id.btnContinueRecover)

        btnRegresarLOGIN = findViewById(R.id.btnRegresarLOGIN)
    }


    private fun disableClicOnSendRecover(email: String) {
        btnContinueRecover.isEnabled =  !email.isNullOrEmpty()

        when(btnContinueRecover.isEnabled){
            true->{
                //btnIniciarSesionLogin.setBackgroundColor( resources.getColor(R.color.black) )
                btnContinueRecover.background = resources.getDrawable(R.drawable.login_register_options_style)
            }

            false->{
                //btnIniciarSesionLogin.setBackgroundColor( resources.getColor(R.color.g_gray500) )
                btnContinueRecover.background = resources.getDrawable(R.drawable.disabled_buttons_style)
            }
        }

    }


    private fun validEmailText(email: String) {
        var disable = true

        if(!email.contains("@") ||  !email.contains(".")){
            etEmailRecover.error = "El correo debe contener almenos un (.) y debe contener (@)"
            disable = false

            btnContinueRecover.isClickable = disable
            //btnIniciarSesionLogin.setBackgroundColor( resources.getColor(R.color.g_gray500) )
            btnContinueRecover.background = resources.getDrawable(R.drawable.disabled_buttons_style)
        }else{
            btnContinueRecover.isClickable = disable
            //btnIniciarSesionLogin.setBackgroundColor( resources.getColor(R.color.black) )
            btnContinueRecover.background = resources.getDrawable(R.drawable.login_register_options_style)
        }
    }

    suspend fun getNewPassword(email: String){

        val objectEmailRecover = RecoverPassword(email)
       try {
            //print("Email to recover: -$email-")
           val retrofitNewPassword = RetrofitHelper.consumeAPI.recoverPassword(objectEmailRecover)
           if(retrofitNewPassword.isSuccessful){
                runOnUiThread {
                    val response = retrofitNewPassword.body().toString()
                    val jsonObject = JSONObject(response)
                    val detailObject = jsonObject.getJSONObject("detail")
                    val msg = detailObject.getString("msg")

                    //NEW
                    val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog, null)
                    val myDialog = Dialog(this@RecoverActivity)
                    myDialog.setContentView(dialogBinding)
                    myDialog.setCancelable(true)
                    myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                    val tvAlertMessage = dialogBinding.findViewById<TextView>(R.id.tvAlertMessage)
                    val continueButton = dialogBinding.findViewById<AppCompatButton>(R.id.alertContinue)
                    myDialog.show()
                    tvAlertMessage.text = msg
                    continueButton.setOnClickListener {
                        myDialog.dismiss()
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    }

                    //END NEW

                    //Toast.makeText(this@RecoverActivity, msg, Toast.LENGTH_LONG).show()

                }

           }else{
               runOnUiThread {
                   val error = retrofitNewPassword.errorBody()?.string()
                   val errorBody = error?.let { JSONObject(it) }
                   val detail = errorBody?.opt("detail")
                   var msg = ""
                   //Log.e("ERROR RECOVER PASS", error.toString() +"  "+ retrofitNewPassword.code())
                   when(detail){
                        is JSONObject->{  msg = detail.getString("msg")  }

                       is JSONArray ->{
                           val firstError = detail.getJSONObject(0)
                           msg = firstError.getString("msg")
                       }
                   }
                   //NEW
                   val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog, null)
                   val myDialog = Dialog(this@RecoverActivity)
                   myDialog.setContentView(dialogBinding)
                   myDialog.setCancelable(true)
                   myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                   val tvAlertMessage = dialogBinding.findViewById<TextView>(R.id.tvAlertMessage)
                   tvAlertMessage.text = msg
                   myDialog.show()
                   val continueButton = dialogBinding.findViewById<AppCompatButton>(R.id.alertContinue)
                   continueButton.setOnClickListener {
                       myDialog.dismiss()
                   }

                   //END NEW
                   //Toast.makeText(this@RecoverActivity, msg, Toast.LENGTH_LONG).show()
               }
           }

       } catch (e:Exception){
           Log.e("ERROR RECOVERING PASSWORDS API: ", e.message.toString())
       }

    }

}