package com.ucenm.conversor.data

data class Conversion(
    val id: Int = 0,
    val fromCode: String,
    val toCode: String,
    val amount: Double,
    val result: Double,
    val date: String,
    var isFavorite: Boolean = false // Para el reto de favoritos
)