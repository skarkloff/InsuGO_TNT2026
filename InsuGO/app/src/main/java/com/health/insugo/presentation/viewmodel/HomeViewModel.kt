package com.health.insugo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.insugo.domain.model.RegistroGlucosa
import com.health.insugo.domain.repository.GlucosaRepository
import com.health.insugo.domain.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private const val DIAS_PROMEDIO_SEMANAL = 7

data class HomeUiState(
    val nombre: String = "",
    val valor: Int? = null,
    val fechaTexto: String = "",
    val estado: String = "",
    val promedioSemanal: Int = 0
)

class HomeViewModel(
    private val repository: GlucosaRepository,
    private val perfilRepository: PerfilRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.obtenerUltimaMedicion(),
                repository.obtenerTodasLasMediciones(),
                perfilRepository.obtenerPerfil()
            ) { ultima, registros, perfil ->
                val promedioSemanal = calcularPromedioSemanal(registros)
                val nombre = perfil?.nombre ?: ""

                if (ultima == null) {
                    HomeUiState(nombre = nombre, promedioSemanal = promedioSemanal)
                } else {
                    HomeUiState(
                        nombre = nombre,
                        valor = ultima.valor,
                        fechaTexto = formatearFecha(ultima.fecha),
                        // Mismos umbrales que HistorialViewModel (170/140 mg/dL)
                        estado = when {
                            ultima.valor >= 170 -> "Alto"
                            ultima.valor >= 140 -> "Atención"
                            else -> "En rango"
                        },
                        promedioSemanal = promedioSemanal
                    )
                }
            }.collect { _uiState.value = it }
        }
    }

    private fun calcularPromedioSemanal(registros: List<RegistroGlucosa>): Int {
        val inicioVentana = inicioDelDia(Calendar.getInstance()).apply {
            add(Calendar.DAY_OF_YEAR, -(DIAS_PROMEDIO_SEMANAL - 1))
        }
        val valores = registros
            .filter { it.fecha.time >= inicioVentana.timeInMillis }
            .map { it.valor }

        return if (valores.isNotEmpty()) valores.average().roundToInt() else 0
    }

    private fun inicioDelDia(calendar: Calendar): Calendar = (calendar.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun formatearFecha(fecha: Date): String {
        val hora = SimpleDateFormat("HH:mm", Locale("es", "ES")).format(fecha)
        val hoy = Calendar.getInstance()
        val diaRegistro = Calendar.getInstance().apply { time = fecha }

        if (mismoDia(hoy, diaRegistro)) return "Hoy $hora"

        val ayer = (hoy.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        if (mismoDia(ayer, diaRegistro)) return "Ayer $hora"

        val fechaCorta = SimpleDateFormat("d MMM", Locale("es", "ES")).format(fecha)
        return "$fechaCorta $hora"
    }

    private fun mismoDia(a: Calendar, b: Calendar): Boolean =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}
