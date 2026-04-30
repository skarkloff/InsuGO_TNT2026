package com.health.insugo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListaViewModel : ViewModel() {
    // StateFlow como holder observable
    private val _alimentos = MutableStateFlow<List<String>>(emptyList())
    val alimentos: StateFlow<List<String>> = _alimentos.asStateFlow()

    init {
        _alimentos.value = listOf(
            "Cerezas frescas de Trelew (Bajo IG)",
            "Manzanas del Valle (Otoño/Invierno)",
            "Cordero Patagónico (Corte magro)",
            "Frutos Rojos (Calafate, Frambuesa)"
        )
    }
}