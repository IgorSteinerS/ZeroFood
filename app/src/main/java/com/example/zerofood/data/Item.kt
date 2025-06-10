package com.example.zerofood.data

import androidx.annotation.DrawableRes

data class Item(
    val name: String,
    @DrawableRes val imageRes: Int
)