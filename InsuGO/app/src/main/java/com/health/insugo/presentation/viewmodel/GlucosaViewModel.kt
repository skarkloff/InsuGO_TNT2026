package com.health.insugo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.insugo.domain.model.RegistroGlucosa
import com.health.insugo.domain.repository.GlucosaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GlucosaViewModel(private val repository: GlucosaRepository) : ViewModel() {
    private val _errorMensaje = MutableStateFlow<String?>(null)
    val errorMensaje = _errorMensaje.asStateFlow()
    private val _valorGlucosa = MutableStateFlow("")
    val valorGlucosa: StateFlow<String> = _valorGlucosa.asStateFlow()

    fun agregarDigito(digito: String) {
        if (_valorGlucosa.value.length < 3) { // Máximo 3 dígitos (ej: 118 mg/dL)
            _valorGlucosa.value += digito
        }
    }

    fun borrarDigito() {
        if (_valorGlucosa.value.isNotEmpty()) {
            _valorGlucosa.value = _valorGlucosa.value.dropLast(1)
        }
    }
    // Asegurate de que la función reciba 'momento: String'
    fun guardarMedicion(momento: String, onExito: () -> Unit) {
        val valorNumerico = _valorGlucosa.value.toIntOrNull() ?: return

        viewModelScope.launch {
            // Ahora sí, le pasamos los 3 parámetros que el repositorio pide
            val resultado = repository.guardarRegistro(
                valor = valorNumerico,
                fechaHora = System.currentTimeMillis(),
                momentoDia = momento // <--- ESTE ES EL QUE FALTABA
            )

            if (resultado.isSuccess) {
                onExito()
            } else {
                _errorMensaje.value =
                    "Error al guardar en la nube: ${resultado.exceptionOrNull()?.message}"// Manejo de error
            }
        }
    }
    fun limpiarError() { _errorMensaje.value = null }
}