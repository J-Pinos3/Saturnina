package com.example.saturninaapp.util

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object RetrofitHelper {

    private val OClient = OkHttpClient.Builder()
        .readTimeout(70, TimeUnit.SECONDS)
        .writeTimeout(150, TimeUnit.SECONDS)
        .connectTimeout(70, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://test-back-dev-nprj.3.us-1.fl0.io/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OClient)
            .build()

    val consumeAPI = retrofit.create(ConsumeAPI::class.java)

}