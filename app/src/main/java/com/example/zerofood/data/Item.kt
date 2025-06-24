package com.example.zerofood.data

import androidx.annotation.DrawableRes

data class Item(
    val id: String = "",
    val nome: String,
    val quantidade: String,
    val data: String,
    val imagemBase64: String
)