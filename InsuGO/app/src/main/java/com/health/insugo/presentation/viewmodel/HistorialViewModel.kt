package com.health.insugo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.insugo.domain.model.RegistroGlucosa
import com.health.insugo.domain.repository.GlucosaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.roundToInt

// Mediciones por debajo de este valor llenan la barra al 100% (referencia visual, no clínica)
private const val ALTURA_MAX_MGDL = 200f
private const val DIAS_HISTORIAL = 7

data class BarraDia(val dia: String, val altura: Float)

data class HistorialUiState(
    val promedio: Int = 0,
    val totalMediciones: Int = 0,
    val barras: List<BarraDia> = emptyList()
)

class HistorialViewModel(private val repository: GlucosaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.obtenerTodasLasMediciones().collect { registros ->
                _uiState.value = calcularEstado(registros)
            }
        }
    }

    private fun calcularEstado(registros: List<RegistroGlucosa>): HistorialUiState {
        val inicioVentana = inicioDelDia(Calendar.getInstance()).apply {
            add(Calendar.DAY_OF_YEAR, -(DIAS_HISTORIAL - 1))
        }
        val registrosVentana = registros.filter { it.fecha.time >= inicioVentana.timeInMillis }

        val barras = (0 until DIAS_HISTORIAL).map { offset ->
            val inicioDia = (inicioVentana.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, offset) }
            val finDia = (inicioDia.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }

            val valoresDelDia = registrosVentana
                .filter { it.fecha.time >= inicioDia.timeInMillis && it.fecha.time < finDia.timeInMillis }
                .map { it.valor }

            val promedioDia = if (valoresDelDia.isNotEmpty()) valoresDelDia.average() else 0.0

            BarraDia(
                dia = etiquetaDia(inicioDia),
                altura = (promedioDia / ALTURA_MAX_MGDL).toFloat().coerceIn(0f, 1f)
            )
        }

        val promedioGeneral = registrosVentana
            .map { it.valor }
            .takeIf { it.isNotEmpty() }
            ?.average()
            ?.roundToInt() ?: 0

        return HistorialUiState(
            promedio = promedioGeneral,
            totalMediciones = registrosVentana.size,
            barras = barras
        )
    }

    private fun inicioDelDia(calendar: Calendar): Calendar = (calendar.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun etiquetaDia(calendar: Calendar): String = when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "D"
        Calendar.MONDAY -> "L"
        Calendar.TUESDAY -> "M"
        Calendar.WEDNESDAY -> "M"
        Calendar.THURSDAY -> "J"
        Calendar.FRIDAY -> "V"
        else -> "S"
    }
}
