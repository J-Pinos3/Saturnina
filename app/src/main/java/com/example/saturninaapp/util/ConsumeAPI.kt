package com.example.saturninaapp.util

import android.hardware.SensorDirectChannel
import com.example.saturninaapp.models.ClothCategoryData
import com.example.saturninaapp.models.ClothCategoryList
import com.example.saturninaapp.models.LoginCredentials
import com.example.saturninaapp.models.UpdateUserProfilePut
import com.example.saturninaapp.models.User
import com.example.saturninaapp.models.UserResponseLogin
import com.example.saturninaapp.models.itemProduct
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @Multipart
    @POST("order")
    suspend fun createOrder(
        @Query("user_id") userId: String,
        @Query("price_order") priceOrder: Double,
        @Query("nombre") nombre: String,
        @Query("apellido") apellido: String,
        @Query("direccion") direccion: String,
        @Query("telefono") telefono: String,
        @Query("descripcion") descripcion: String,
        @Part image: MultipartBody.Part,
        @Part("products") products: List<MultipartBody.Part>
        ): Response<JsonObject>
}