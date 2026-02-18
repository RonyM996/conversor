package com.ucenm.conversor.data

data class Rate(
    val id: Int = 0,
    val fromCode: String,
    val toCode: String,
    val rate: Double,
    val isCustom: Boolean = false // Para identificar tasas creadas por el usuario
)