package com.example.saturninaapp.models

data class IdOrden(
    val apellido: String,
    val descripcion: String,
    val direccion: String,
    val email: String,
    val id: String,
    val image_transaccion: Imagen,
    val nombre: String,
    val order_date: String,
    val price_order: Double,
    val telefono: String,
    val user_id: String
)