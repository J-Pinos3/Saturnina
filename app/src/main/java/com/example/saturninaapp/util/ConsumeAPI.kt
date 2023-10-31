package com.example.saturninaapp.util

import com.example.saturninaapp.models.User
import retrofit2.http.POST

interface ConsumeAPI {

    @POST("/register")
    fun createUser(user: User): User

}