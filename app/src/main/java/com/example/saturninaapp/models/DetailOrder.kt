package com.example.saturninaapp.models

data class DetailOrder(
    val result: List<OrderResult>,
    val status: String,
    val time: String
)