package com.example.saturninaapp.models

import java.io.Serializable

data class IdProducto(
    val category: String = "",
    val colores: List<Colore> = emptyList(),
    val descripcion: String = "",
    val id: String = "",
    val imagen: List<Imagen> = emptyList(),
    val name: String = "",
    val precio: Double = 0.0,
    val tallas: List<Talla> = emptyList()
): Serializable