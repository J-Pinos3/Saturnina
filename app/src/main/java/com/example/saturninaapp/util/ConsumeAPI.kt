package com.example.saturninaapp.util

import com.example.saturninaapp.models.ClothCategoryList
import com.example.saturninaapp.models.CommentaryData
import com.example.saturninaapp.models.CommentsRawList
import com.example.saturninaapp.models.LoginCredentials
import com.example.saturninaapp.models.OrdersList
import com.example.saturninaapp.models.UpdateUserProfilePut
import com.example.saturninaapp.models.User
import com.example.saturninaapp.models.UserResponseLogin
import com.example.saturninaapp.models.itemProduct
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Header("Authorization") authorization: String,
        @Part("data") data: RequestBody,
        @Part transfer_image: MultipartBody.Part

        ): Response<JsonObject>


    @Headers("Content-type:application/json; charset=UTF-8")
    @GET("orders")
    suspend fun getAllOrders(@Header("Authorization") authorization: String): Response<OrdersList>


    @Headers("Content-type:application/json; charset=UTF-8")
    @GET("order/{id_user}")
    suspend fun getOrdersForUser(
        @Header("Authorizarion") authorization: String,
        @Path("id_user") userId: String
    ): Response<OrdersList>


    @Multipart
    @POST("order/{id_user}")
    suspend fun updateUserOrder(
        @Header("Authorizarion") authorization: String,
        @Part("data") data: RequestBody,
        @Part transfer_image: MultipartBody.Part,
        @Path("id_user") userId: String
    ): Response<JsonObject>



    @Headers("Content-type:application/json; charset=UTF-8")
    @GET("comments")
    suspend fun getAllComments(@Header("Authorization") authorization: String ): Response<CommentsRawList>


    @Headers("Content-type:application/json; charset=UTF-8")
    @POST("comments")
    suspend fun createCommentary(
        @Header("Authorization") authorization: String,
        @Body commentaryData: CommentaryData
    ): Response<JsonObject>
}

/*
*     @Multipart
    @POST("order")
    suspend fun createOrder(
        @Header("Authorization") authorization: String,
        @Part("user_id") userId: RequestBody,
        @Part("price_order") priceOrder: RequestBody,
        @Part products: List<MultipartBody.Part>,
        @Part("nombre") nombre: RequestBody,
        @Part("apellido") apellido: RequestBody,
        @Part("direccion") direccion: RequestBody,
        @Part("email") email: RequestBody,
        @Part("telefono") telefono: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part transfer_image: MultipartBody.Part

        ): Response<JsonObject>
* */