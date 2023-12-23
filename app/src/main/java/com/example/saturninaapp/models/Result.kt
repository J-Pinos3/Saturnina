package com.example.saturninaapp.models

data class Result(
    val cantidad: Int,
    val fecha: String,
    val id: String,
    val id_orden: IdOrden,
    val id_producto: IdProducto,
    val status: String
)