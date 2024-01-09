package com.example.saturninaapp.models

import com.example.saturninaapp.util.AlwaysListTypeAdapterFactory
import com.google.gson.annotations.JsonAdapter
import java.io.Serializable

data class DetailProduct(
    val category: String = "",
    val colores: List<Colore>? =  emptyList(),
    val descripcion: String = "",
    val id: String = "",
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    val imagen: List<Imagen> = emptyList(),
    val name: String = "",
    val precio: Double = 0.0,
    val tallas: List<Talla>? =  emptyList(),
    var tallaSeleccionada: String = "",
    var colorSeleccionado: String = "",
    var contador: Int = 0//unidades pedidas
): Serializable

