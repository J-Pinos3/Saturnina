package com.example.saturninaapp.util

import com.example.saturninaapp.models.LoginCredentials
import com.example.saturninaapp.models.User
import com.example.saturninaapp.models.UserResponseLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ConsumeAPI {

    @Headers("Content-type:application/json; charset=UTF-8")
    @POST("register")
    suspend fun createUser( @Body p:User ): Response<String>

    @Headers("Content-type:application/json; charset=UTF-8")
    @POST("login")
    suspend fun loginUser(@Body credentials: LoginCredentials): Response<UserResponseLogin>

}