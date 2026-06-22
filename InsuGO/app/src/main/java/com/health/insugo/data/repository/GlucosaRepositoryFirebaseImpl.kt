package com.health.insugo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.health.insugo.domain.model.RegistroGlucosa
import com.health.insugo.domain.repository.GlucosaRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID

class GlucosaRepositoryFirebaseImpl(
    val firestore: FirebaseFirestore,
    val auth: FirebaseAuth
) : GlucosaRepository {

    private fun coleccionUsuario() = auth.currentUser?.uid?.let { userId ->
        firestore.collection("usuarios").document(userId).collection("registros")
    }

    // Agregamos 'override' porque esta función está en la interfaz
    override suspend fun guardarRegistro(valor: Int, fechaHora: Long, momentoDia: String): Result<Unit> {
        val coleccion = coleccionUsuario() ?: return Result.failure(Exception("No logueado"))

        return try {
            val registroId = UUID.randomUUID().toString()
            val datos = hashMapOf(
                "id" to registroId,
                "valor" to valor,
                "fechaHora" to fechaHora,
                "momentoDia" to momentoDia // ¡Ahora sí lo guardamos!
            )
            coleccion.document(registroId).set(datos).await()
            println("DEBUG_INSUGO: Dato guardado con éxito: valor=$valor, momento=$momentoDia")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    override fun obtenerTodasLasMediciones(): Flow<List<RegistroGlucosa>> = callbackFlow {
        val coleccion = coleccionUsuario()

        if (coleccion == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        // Movemos el listener acá directamente
        val listener = coleccion.orderBy("fechaHora", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Ocurre, por ejemplo, al cerrar sesión: Firestore corta el listener con
                    // PERMISSION_DENIED. No relanzamos para no tirar abajo al colector.
                    close()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Convertimos directamente a RegistroGlucosa
                    val registros = snapshot.documents.mapNotNull { doc ->
                        val mapa = doc.data ?: return@mapNotNull null
                        RegistroGlucosa(
                            id = mapa["id"] as? String ?: "",
                            valor = (mapa["valor"] as? Long)?.toInt() ?: 0,
                            momentoDia = mapa["momentoDia"] as? String ?: "Otro",
                            fecha = Date((mapa["fechaHora"] as? Long) ?: 0L) // Asegurate de usar "fechaHora"
                        )
                    }
                    trySend(registros)
                }
            }
        awaitClose { listener.remove() }
    }
    override fun obtenerUltimaMedicion(): Flow<RegistroGlucosa?> {
        // Reutilizamos el Flow anterior pero tomamos solo el primero de la lista (el más reciente)
        return obtenerTodasLasMediciones().map { lista ->
            lista.firstOrNull()
        }
    }
}