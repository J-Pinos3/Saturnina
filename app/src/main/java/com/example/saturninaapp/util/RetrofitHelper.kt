package com.example.saturninaapp.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitHelper {

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://test-back-4kx4.onrender.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val consumeAPI = retrofit.create(ConsumeAPI::class.java)

}