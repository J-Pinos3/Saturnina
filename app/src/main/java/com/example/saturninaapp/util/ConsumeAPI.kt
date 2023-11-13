package com.example.saturninaapp.util

import com.example.saturninaapp.models.ClothCategoryData
import com.example.saturninaapp.models.ClothCategoryList
import com.example.saturninaapp.models.LoginCredentials
import com.example.saturninaapp.models.UpdateUserProfilePut
import com.example.saturninaapp.models.User
import com.example.saturninaapp.models.UserResponseLogin
import com.example.saturninaapp.models.itemProduct
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @Headers("Content-type:application/json; charset=UTF-8")
    @PUT("user/{id}")
    suspend fun updateUserProfile(
        @Header("Authorization")  authorization: String,
        @Path("id") userId: String,
        @Body userProfilePut: UpdateUserProfilePut
        ): Response<JsonObject>


    @Headers("Content-type:application/json; charset=UTF-8")
    @GET("category")
    suspend fun getClothesCategories(@Header("Authorization")  authorization: String,
    ): Response<ClothCategoryList>


    @Headers("Content-type: application/json; charset=UTF-8")
    @GET("products")
    suspend fun getItemsProducts(@Header("Authorization") authorization: String): Response<itemProduct>
}