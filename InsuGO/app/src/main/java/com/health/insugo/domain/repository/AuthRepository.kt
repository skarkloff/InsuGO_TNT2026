package com.health.insugo.domain

import com.health.insugo.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observarUsuarioActual(): Flow<Usuario?>
    suspend fun iniciarSesion(email: String, clave: String): Result<Usuario>

    suspend fun iniciarSesionConGoogle(idToken: String): Result<Usuario>
    suspend fun cerrarSesion()

}