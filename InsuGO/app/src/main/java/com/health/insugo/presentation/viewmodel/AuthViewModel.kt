package com.health.insugo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.insugo.domain.AuthRepository
import com.health.insugo.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Guarda el estado del usuario actual (si es null, no hay sesión iniciada)
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    init {
        // Apenas nace el ViewModel, se pone a escuchar si hay un usuario logueado
        observarSesion()
    }

    private fun observarSesion() {
        viewModelScope.launch {
            authRepository.observarUsuarioActual().collect { usuario ->
                _usuarioActual.value = usuario
            }
        }
    }

    // Llama al repositorio para iniciar sesión y devuelve el resultado a la pantalla
    fun iniciarSesion(email: String, clave: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.iniciarSesion(email, clave)
            if (result.isSuccess) {
                onResult(true, null) // Éxito, no hay mensaje de error
            } else {
                onResult(false, result.exceptionOrNull()?.message) // Falló, pasamos el error
            }
        }
    }

    fun registrarse(email: String, clave: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.registrarse(email, clave)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message)
            }
        }
    }

    fun iniciarSesionConGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.iniciarSesionConGoogle(idToken)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message)
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            authRepository.cerrarSesion()
        }
    }

    fun reautenticar(clave: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.reautenticar(clave)
            onResult(result.isSuccess, result.exceptionOrNull()?.message)
        }
    }

    fun actualizarEmail(nuevoEmail: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.actualizarEmail(nuevoEmail)
            onResult(result.isSuccess, result.exceptionOrNull()?.message)
        }
    }

    fun actualizarClave(nuevaClave: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.actualizarClave(nuevaClave)
            onResult(result.isSuccess, result.exceptionOrNull()?.message)
        }
    }
}