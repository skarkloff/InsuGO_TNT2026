package com.health.insugo.domain.model

import java.util.Date

data class Comida(
    val id: String = "",
    val desayuno: String,
    val almuerzo: String,
    val cena: String,
    val actividad: String,
    val minutosActividad: Int,
    val fecha: Date = Date()
)
