package com.example.saturninaapp.models

import java.io.Serializable

data class OrderResult(
    val cantidad: Int,
    val color: String,
    val fecha: String,
    val id: String,
    val id_orden: IdOrden,
    val id_producto: IdProducto,
    val status: String,
    val talla: String
): Serializable