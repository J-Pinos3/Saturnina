package com.example.saturninaapp.models

data class ResultComment(
    val calificacion: Int = 0,
    val descripcion: String = "",
    val id: String? = "",
    val id_producto: String = "",
    val user_id: UserId = UserId()
)