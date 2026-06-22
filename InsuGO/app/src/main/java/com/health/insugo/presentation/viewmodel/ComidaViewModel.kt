package com.health.insugo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.insugo.domain.model.Comida
import com.health.insugo.domain.repository.ComidaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ComidaViewModel(private val repository: ComidaRepository) : ViewModel() {
    private val _errorMensaje = MutableStateFlow<String?>(null)
    val errorMensaje = _errorMensaje.asStateFlow()

    private val _desayuno = MutableStateFlow("")
    val desayuno = _desayuno.asStateFlow()

    private val _almuerzo = MutableStateFlow("")
    val almuerzo = _almuerzo.asStateFlow()

    private val _cena = MutableStateFlow("")
    val cena = _cena.asStateFlow()

    private val _actividad = MutableStateFlow("Caminar")
    val actividad = _actividad.asStateFlow()

    private val _minutos = MutableStateFlow(30f)
    val minutos = _minutos.asStateFlow()

    fun actualizarDesayuno(valor: String) { _desayuno.value = valor }
    fun actualizarAlmuerzo(valor: String) { _almuerzo.value = valor }
    fun actualizarCena(valor: String) { _cena.value = valor }
    fun seleccionarActividad(valor: String) { _actividad.value = valor }
    fun actualizarMinutos(valor: Float) { _minutos.value = valor }

    fun guardarComida(onExito: () -> Unit) {
        viewModelScope.launch {
            val comida = Comida(
                desayuno = _desayuno.value,
                almuerzo = _almuerzo.value,
                cena = _cena.value,
                actividad = _actividad.value,
                minutosActividad = _minutos.value.toInt()
            )

            val resultado = repository.guardarComida(comida)

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
