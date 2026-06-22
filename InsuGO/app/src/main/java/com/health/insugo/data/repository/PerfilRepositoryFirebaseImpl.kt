package com.health.insugo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.health.insugo.domain.model.Perfil
import com.health.insugo.domain.repository.PerfilRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PerfilRepositoryFirebaseImpl(
    val firestore: FirebaseFirestore,
    val auth: FirebaseAuth
) : PerfilRepository {

    override suspend fun guardarPerfil(perfil: Perfil): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("No logueado"))

        return try {
            val datos = hashMapOf(
                "nombre" to perfil.nombre,
                "diagnostico" to perfil.diagnostico
            )
            firestore.collection("usuarios").document(userId)
                .set(datos, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun obtenerPerfil(): Flow<Perfil?> = callbackFlow {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("usuarios").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Ocurre, por ejemplo, al cerrar sesión: Firestore corta el listener con
                    // PERMISSION_DENIED. No relanzamos para no tirar abajo al colector.
                    close()
                    return@addSnapshotListener
                }

                val nombre = snapshot?.getString("nombre")
                val diagnostico = snapshot?.getString("diagnostico")

                trySend(
                    if (nombre != null && diagnostico != null) {
                        Perfil(nombre = nombre, diagnostico = diagnostico)
                    } else {
                        null
                    }
                )
            }
        awaitClose { listener.remove() }
    }
}
