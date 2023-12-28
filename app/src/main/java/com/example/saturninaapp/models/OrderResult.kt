package com.example.saturninaapp.models

import java.io.Serializable

data class OrderResult(
    val cantidad: Int = 0,
    val color: String = "",
    val fecha: String = "",
    val id: String = "",
    val id_orden: IdOrden = IdOrden(),
    val id_producto: IdProducto = IdProducto(),
    val status: String = "",
    val talla: String = ""

):Serializable