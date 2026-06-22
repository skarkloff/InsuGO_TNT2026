package com.health.insugo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.health.insugo.domain.model.Comida
import com.health.insugo.domain.repository.ComidaRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ComidaRepositoryFirebaseImpl(
    val firestore: FirebaseFirestore,
    val auth: FirebaseAuth
) : ComidaRepository {

    private fun coleccionUsuario() = auth.currentUser?.uid?.let { userId ->
        firestore.collection("usuarios").document(userId).collection("comidas")
    }

    override suspend fun guardarComida(comida: Comida): Result<Unit> {
        val coleccion = coleccionUsuario() ?: return Result.failure(Exception("No logueado"))

        return try {
            val comidaId = UUID.randomUUID().toString()
            val datos = hashMapOf(
                "id" to comidaId,
                "desayuno" to comida.desayuno,
                "almuerzo" to comida.almuerzo,
                "cena" to comida.cena,
                "actividad" to comida.actividad,
                "minutosActividad" to comida.minutosActividad,
                "fecha" to comida.fecha.time
            )
            coleccion.document(comidaId).set(datos).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
