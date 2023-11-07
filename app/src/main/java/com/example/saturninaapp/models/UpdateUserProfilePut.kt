package com.example.saturninaapp.models

data class UpdateUserProfilePut(
    val apellido: String,
    val email: String,
    val nombre: String,
    val telefono: String
)