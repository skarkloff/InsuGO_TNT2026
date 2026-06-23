package com.health.insugo.domain.model

// Esta es tu entidad de negocio pura, sin dependencias de Firebase
data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val esCuentaGoogle: Boolean = false
)