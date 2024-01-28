package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.saturninaapp.R
import com.example.saturninaapp.util.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class RecoverActivity : AppCompatActivity() {

    private lateinit var etEmailRecover: EditText
    private lateinit var btnContinueRecover: AppCompatButton


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

        btnContinueRecover.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                getNewPassword(etEmailRecover.text.toString())
            }
        }


    }//ON CREATE

    private fun initUI() {
        etEmailRecover = findViewById(R.id.etEmailRecover)
        btnContinueRecover = findViewById(R.id.btnContinueRecover)
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
       try {

           val retrofitNewPassword = RetrofitHelper.consumeAPI.recoverPassword(email)
           if(retrofitNewPassword.isSuccessful){
                runOnUiThread {
                    val response = retrofitNewPassword.body().toString()
                    val jsonObject = JSONObject(response)
                    val detailObject = jsonObject.getJSONObject("detail")
                    val msg = detailObject.getString("msg")

                    Toast.makeText(this@RecoverActivity, msg, Toast.LENGTH_LONG).show()

                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                }

           }else{
               runOnUiThread {
                   val error = retrofitNewPassword.errorBody()?.string()
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
                   Toast.makeText(this@RecoverActivity, msg, Toast.LENGTH_LONG).show()
               }
           }

       } catch (e:Exception){
           Log.e("ERROR RECOVERING PASSWORDS API: ", e.message.toString())
       }

    }

}