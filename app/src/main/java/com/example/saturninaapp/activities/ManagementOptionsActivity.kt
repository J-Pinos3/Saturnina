package com.example.saturninaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.saturnina.saturninaapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ManagementOptionsActivity : AppCompatActivity() {

    private lateinit var btnSalesManagementOptions: AppCompatButton
    private lateinit var btnBackManagementOptions: AppCompatButton

    lateinit var bottom_nav_management_options: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_management_options)
        initUI()

        val user_token = intent.extras?.getString("USER_TOKEN")
        val user_id = intent.extras?.getString("USER_ID")
        val user_rol = intent.extras?.getString("USER_ROL")

        //println("USER DATA: $user_id, $user_rol, + $user_token")

        btnSalesManagementOptions.setOnClickListener {
            val intent = Intent(applicationContext, MySalesActivity::class.java)
            intent.putExtra("USER_TOKEN", user_token)
            intent.putExtra("USER_ID", user_id)
            intent.putExtra("USER_ROL", user_rol)
            startActivity(intent)
        }


        btnBackManagementOptions.setOnClickListener {
            val intent = Intent(applicationContext, IntroDashboardNews::class.java)
            intent.putExtra("USER_TOKEN", user_token)
            intent.putExtra("USER_ID", user_id)
            intent.putExtra("USER_ROL", user_rol)
            startActivity(intent)
        }


        bottom_nav_management_options.setOnNavigationItemSelectedListener{

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
        btnSalesManagementOptions = findViewById(R.id.btnSalesManagementOptions)
        btnBackManagementOptions = findViewById(R.id.btnBackManagementOptions)

        bottom_nav_management_options = findViewById(R.id.bottom_nav_management_options)
    }


}