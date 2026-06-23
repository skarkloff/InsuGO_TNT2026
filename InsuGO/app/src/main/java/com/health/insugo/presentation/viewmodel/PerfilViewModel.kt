package com.health.insugo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.insugo.domain.model.Perfil
import com.health.insugo.domain.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PerfilViewModel(private val repository: PerfilRepository) : ViewModel() {
    private val _errorMensaje = MutableStateFlow<String?>(null)
    val errorMensaje = _errorMensaje.asStateFlow()

    private val _nombre = MutableStateFlow("")
    val nombre = _nombre.asStateFlow()

    private val _diagnostico = MutableStateFlow("Diabetes tipo 2")
    val diagnostico = _diagnostico.asStateFlow()

    private val _ubicacionActivada = MutableStateFlow(false)
    val ubicacionActivada = _ubicacionActivada.asStateFlow()

    init {
        viewModelScope.launch {
            repository.obtenerPerfil().firstOrNull()?.let { perfil ->
                _nombre.value = perfil.nombre
                _diagnostico.value = perfil.diagnostico
                _ubicacionActivada.value = perfil.ubicacionActivada
            }
        }
    }

    fun actualizarNombre(valor: String) { _nombre.value = valor }
    fun seleccionarDiagnostico(valor: String) { _diagnostico.value = valor }
    fun seleccionarUbicacionActivada(valor: Boolean) { _ubicacionActivada.value = valor }

    fun guardarPerfil(onExito: () -> Unit) {
        viewModelScope.launch {
            val perfil = Perfil(
                nombre = _nombre.value,
                diagnostico = _diagnostico.value,
                ubicacionActivada = _ubicacionActivada.value
            )

            val resultado = repository.guardarPerfil(perfil)

            if (resultado.isSuccess) {
                onExito()
            } else {
                _errorMensaje.value =
                    "Error al guardar en la nube: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }

    fun limpiarError() { _errorMensaje.value = null }
}
