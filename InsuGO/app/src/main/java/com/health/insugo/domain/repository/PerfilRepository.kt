package com.health.insugo.domain.repository

import com.health.insugo.domain.model.Perfil
import kotlinx.coroutines.flow.Flow

interface PerfilRepository {

    // Para guardar el perfil del usuario (lo que llama el ViewModel)
    suspend fun guardarPerfil(perfil: Perfil): Result<Unit>

    // Para leer el perfil del usuario (nombre, diagnóstico)
    fun obtenerPerfil(): Flow<Perfil?>
}
