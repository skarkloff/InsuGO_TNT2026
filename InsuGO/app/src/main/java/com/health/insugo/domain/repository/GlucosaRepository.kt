package com.health.insugo.domain.repository

import com.health.insugo.domain.model.RegistroGlucosa
import kotlinx.coroutines.flow.Flow

interface GlucosaRepository {

    // Para guardar datos (lo que llama el ViewModel)
    suspend fun guardarRegistro(valor: Int, fechaHora: Long, momentoDia: String): Result<Unit>

    // Para obtener el historial completo (lo que usa la pantalla de lista)
    fun obtenerTodasLasMediciones(): Flow<List<RegistroGlucosa>>

    // Para obtener la última medición (para el home)
    fun obtenerUltimaMedicion(): Flow<RegistroGlucosa?>
}