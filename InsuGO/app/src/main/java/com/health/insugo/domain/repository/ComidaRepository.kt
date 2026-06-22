package com.health.insugo.domain.repository

import com.health.insugo.domain.model.Comida

interface ComidaRepository {

    // Para guardar el registro del día (lo que llama el ViewModel)
    suspend fun guardarComida(comida: Comida): Result<Unit>
}
