package com.example.saturninaapp.util

import com.example.saturninaapp.models.LoginCredentials
import com.example.saturninaapp.models.User
import com.example.saturninaapp.models.UserResponseLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ConsumeAPI {

    @Headers("Content-type:application/json; charset=UTF-8")
    @POST("register")
    suspend fun createUser( @Body p:User ): Response<String>

    @Headers("Content-type:application/json; charset=UTF-8")
    @POST("login")
    suspend fun loginUser(@Body credentials: LoginCredentials): Response<UserResponseLogin>

    //how to add authorization: bearer
    @Headers("Content-type:application/json; charset=UTF-8")
    @GET("profile")
    suspend fun getUserProfile(@Header("Authorization") authorization:String): Response<UserResponseLogin>//I Use UserResponseLogin because it gives the same data as LoginUser
}