package com.health.insugo.presentation.export

import android.content.Context
import com.health.insugo.presentation.viewmodel.HistorialUiState

interface PdfExporter {
    fun exportarHistorialPdf(context: Context, state: HistorialUiState, nombreUsuario: String)
}
