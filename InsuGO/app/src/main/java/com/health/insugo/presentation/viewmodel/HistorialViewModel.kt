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

import android.content.Context
import android.content.Intent
import com.health.insugo.domain.repository.PerfilRepository
import com.health.insugo.presentation.export.PdfExporter
import kotlinx.coroutines.flow.firstOrNull

import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Locale

// Mediciones por debajo de este valor llenan la barra al 100% (referencia visual, no clínica)
private const val ALTURA_MAX_MGDL = 200f
private const val DIAS_HISTORIAL = 7

data class BarraDia(val dia: String, val altura: Float)

data class HistorialUiState(
    val promedio: Int = 0,
    val totalMediciones: Int = 0,
    val barras: List<BarraDia> = emptyList(),
    val nombre: String = "",
    val mediciones: List<RegistroGlucosa> = emptyList()
)

class HistorialViewModel(
    private val repository: GlucosaRepository,
    private val perfilRepository: PerfilRepository,
    private val pdfExporter: PdfExporter
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState = _uiState.asStateFlow()

    fun exportarACompPDF(context: Context) {
        viewModelScope.launch {
            val perfil = perfilRepository.obtenerPerfil().firstOrNull()
            val nombre = perfil?.nombre ?: "Usuario de InsuGO"
            pdfExporter.exportarHistorialPdf(context, _uiState.value, nombre)
        }
    }

    fun compartirPorWhatsApp(context: Context) {
        viewModelScope.launch {
            val perfil = perfilRepository.obtenerPerfil().firstOrNull()
            val nombre = perfil?.nombre ?: "Usuario de InsuGO"
            val textReport = construirTextoReporte(nombre, _uiState.value)
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, textReport)
                setPackage("com.whatsapp")
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback a share sheet general
                val chooserIntent = Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, textReport)
                    },
                    "Compartir reporte"
                )
                context.startActivity(chooserIntent)
            }
        }
    }

    private fun construirTextoReporte(nombre: String, estado: HistorialUiState): String {
        val sdfHoy = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
        val fechaHoy = sdfHoy.format(java.util.Date())

        val sb = StringBuilder()
        sb.append("*REPORTE SEMANAL - InsuGO*\n")
        sb.append("👤 Paciente: ${nombre.ifBlank { "Usuario de InsuGO" }}\n")
        sb.append("📅 Fecha de generación: $fechaHoy\n\n")
        
        sb.append("*📊 RESUMEN DE LA SEMANA*\n")
        sb.append("• Promedio glucemia: ${estado.promedio} mg/dL\n")
        sb.append("• Total mediciones: ${estado.totalMediciones}\n\n")

        if (estado.mediciones.isNotEmpty()) {
            sb.append("*📋 ÚLTIMAS MEDICIONES*\n")
            val sdfMedicion = SimpleDateFormat("dd/MM HH:mm", Locale("es", "ES"))
            estado.mediciones.forEach { med ->
                val fechaMed = sdfMedicion.format(med.fecha)
                val alerta = when {
                    med.valor >= 170 -> "🚨"
                    med.valor >= 140 -> "⚠️"
                    else -> "✅"
                }
                sb.append("$alerta $fechaMed - ${med.momentoDia}: ${med.valor} mg/dL\n")
            }
            sb.append("\n")
        }

        sb.append("_Generado automáticamente por InsuGO. Este reporte es orientativo y no reemplaza la consulta médica._")
        return sb.toString()
    }

    init {
        viewModelScope.launch {
            combine(
                repository.obtenerTodasLasMediciones(),
                perfilRepository.obtenerPerfil()
            ) { registros, perfil ->
                val estado = calcularEstado(registros)
                estado.copy(nombre = perfil?.nombre ?: "Usuario")
            }.collect {
                _uiState.value = it
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

        val ultimas5 = registros.sortedByDescending { it.fecha.time }.take(5)

        return HistorialUiState(
            promedio = promedioGeneral,
            totalMediciones = registrosVentana.size,
            barras = barras,
            mediciones = ultimas5
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
