package com.example.saturninaapp.models

data class DetailProduct(
    val category: String,
    val descripcion: String,
    val id: String,
    val imagen: Imagen,
    val name: String,
    val precio: Double,
    var contador: Int = 0//unidades pedidas
)