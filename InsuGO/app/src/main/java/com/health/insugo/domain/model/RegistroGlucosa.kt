package com.health.insugo.domain.model

import java.util.Date

data class RegistroGlucosa(
    val id: String = "",         // Cambiamos a String porque Firebase usa IDs alfanuméricos
    val valor: Int,
    val momentoDia: String,
    val fecha: Date = Date()
)