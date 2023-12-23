package com.example.saturninaapp.models

import com.example.saturninaapp.util.AlwaysListTypeAdapterFactory
import com.google.gson.annotations.JsonAdapter

data class DetailProduct(
    val category: String,
    val colores: List<Colore>,
    val descripcion: String,
    val id: String,
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    val imagen: List<Imagen>,
    val name: String,
    val precio: Double,
    val tallas: List<Talla>,
    var tallaSeleccionada: String = "",
    val colorSeleccionado: String = "",
    var contador: Int = 0//unidades pedidas
)

