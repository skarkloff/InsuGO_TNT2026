package com.health.insugo.data.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.health.insugo.domain.AuthRepository
import com.health.insugo.domain.model.Usuario

class AuthRepositoryFirebaseImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private fun mapearUsuario(firebaseUser: FirebaseUser?): Usuario? {
        return firebaseUser?.let {
            Usuario(
                id = it.uid,
                nombre = it.displayName ?: "Usuario",
                email = it.email ?: ""
            )
        }
    }

    override fun observarUsuarioActual(): Flow<Usuario?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(mapearUsuario(auth.currentUser))
        }
        firebaseAuth.addAuthStateListener(listener)

        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun iniciarSesion(email: String, clave: String): Result<Usuario> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, clave).await()
            val user = mapearUsuario(result.user)

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("No se pudieron obtener los datos del usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registrarse(email: String, clave: String): Result<Usuario> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, clave).await()
            val user = mapearUsuario(result.user)

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("No se pudo crear la cuenta"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun iniciarSesionConGoogle(idToken: String): Result<Usuario> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = mapearUsuario(result.user)

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("No se pudieron obtener los datos de Google"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cerrarSesion() {
        firebaseAuth.signOut()
    }

    override suspend fun reautenticar(clave: String): Result<Unit> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No logueado"))
        val email = user.email ?: return Result.failure(Exception("La cuenta no tiene un correo asociado"))

        return try {
            val credential = EmailAuthProvider.getCredential(email, clave)
            user.reauthenticate(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun actualizarEmail(nuevoEmail: String): Result<Unit> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No logueado"))

        return try {
            // Envía un correo de verificación a la nueva dirección; el cambio se aplica
            // cuando el usuario lo confirma (Firebase ya no permite cambiarlo sin verificar)
            user.verifyBeforeUpdateEmail(nuevoEmail).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun actualizarClave(nuevaClave: String): Result<Unit> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No logueado"))

        return try {
            user.updatePassword(nuevaClave).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}