package com.example.saturninaapp.models

import com.google.gson.annotations.SerializedName

data class CommentaryData(

    @SerializedName("descripcion") val description: String = "",
    val user_id: String = "",
    val id_producto: String? ="",
    val calificacion: Int = 0

)
