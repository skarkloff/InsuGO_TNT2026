package com.health.insugo.domain

import com.health.insugo.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observarUsuarioActual(): Flow<Usuario?>
    suspend fun iniciarSesion(email: String, clave: String): Result<Usuario>
    suspend fun registrarse(email: String, clave: String): Result<Usuario>

    suspend fun iniciarSesionConGoogle(idToken: String): Result<Usuario>
    suspend fun cerrarSesion()

    // Firebase exige reautenticar antes de cambiar correo o contraseña
    suspend fun reautenticar(clave: String): Result<Unit>
    suspend fun actualizarEmail(nuevoEmail: String): Result<Unit>
    suspend fun actualizarClave(nuevaClave: String): Result<Unit>
}