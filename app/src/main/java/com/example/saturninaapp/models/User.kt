package com.example.saturninaapp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class User(
    var nombre: String = "",
    var apellido: String = "",
    var telefono: String = "",
    @SerializedName("email") var correo: String = "",
    var password: String = "",


):Serializable
