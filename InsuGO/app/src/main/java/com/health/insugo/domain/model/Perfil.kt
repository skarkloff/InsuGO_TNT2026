package com.health.insugo.domain.model

data class Perfil(
    val nombre: String,
    val diagnostico: String,
    val ubicacionActivada: Boolean = false
)
