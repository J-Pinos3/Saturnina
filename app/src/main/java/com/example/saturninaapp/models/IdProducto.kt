package com.example.saturninaapp.models

data class IdProducto(
    val category: String,
    val colores: List<Colore>,
    val descripcion: String,
    val id: String,
    val imagen: List<Imagen>,
    val name: String,
    val precio: Double,
    val tallas: List<Talla>
)