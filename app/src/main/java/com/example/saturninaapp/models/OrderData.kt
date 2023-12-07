package com.example.saturninaapp.models

data class OrderData(
    val apellido: String,
    val descripcion: String,
    val direccion: String,
    val email: String,
    val nombre: String,
    val price_order: Double,
    val products: List<Product>,
    val telefono: String,
    val user_id: String
)