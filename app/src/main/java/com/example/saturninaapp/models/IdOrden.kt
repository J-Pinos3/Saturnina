package com.example.saturninaapp.models

import java.io.Serializable

data class IdOrden(
    val apellido: String = "",
    val descripcion: String = "",
    val direccion: String = "",
    val email: String = "",
    val id: String = "",
    val image_transaccion: Imagen = Imagen(),
    val nombre: String = "",
    val order_date: String = "",
    val price_order: Double = 0.0,
    val telefono: String = "",
    val user_id: String = ""
): Serializable