package com.health.insugo.presentation.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import com.health.insugo.presentation.viewmodel.HistorialUiState
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AndroidPdfExporter : PdfExporter {

    override fun exportarHistorialPdf(context: Context, state: HistorialUiState, nombreUsuario: String) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "InsuGO_Reporte_${nombreUsuario.replace(" ", "_")}"

        printManager.print(
            jobName,
            HistorialPrintAdapter(context, state, nombreUsuario),
            null
        )
    }

    private class HistorialPrintAdapter(
        private val context: Context,
        private val state: HistorialUiState,
        private val nombreUsuario: String
    ) : PrintDocumentAdapter() {

        private var pdfDocument: PrintedPdfDocument? = null

        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes,
            cancellationSignal: CancellationSignal?,
            callback: LayoutResultCallback,
            extras: Bundle?
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback.onLayoutCancelled()
                return
            }

            pdfDocument = PrintedPdfDocument(context, newAttributes)

            val info = PrintDocumentInfo.Builder("reporte_insugo.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build()

            callback.onLayoutFinished(info, true)
        }

        override fun onWrite(
            pages: Array<out PageRange>?,
            destination: ParcelFileDescriptor,
            cancellationSignal: CancellationSignal?,
            callback: WriteResultCallback
        ) {
            val pdf = pdfDocument ?: return
            val page = pdf.startPage(0)

            if (cancellationSignal?.isCanceled == true) {
                callback.onWriteCancelled()
                pdf.close()
                pdfDocument = null
                return
            }

            val canvas = page.canvas
            drawPdfContent(canvas, page.info.pageWidth, page.info.pageHeight)

            pdf.finishPage(page)

            try {
                pdf.writeTo(FileOutputStream(destination.fileDescriptor))
                callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            } catch (e: IOException) {
                callback.onWriteFailed(e.toString())
            } finally {
                pdf.close()
                pdfDocument = null
            }
        }

        private fun drawPdfContent(canvas: Canvas, width: Int, height: Int) {
            val widthF = width.toFloat()
            val heightF = height.toFloat()

            // Fondo de la página entero en un tono muy claro de fondo si se desea, o blanco para ahorrar tinta
            val bgPaint = Paint().apply {
                color = Color.WHITE
                style = Paint.Style.FILL
            }
            canvas.drawRect(0f, 0f, widthF, heightF, bgPaint)

            // 1. Encabezado con color corporativo Verde (0xFF1D9E75)
            val headerPaint = Paint().apply {
                color = 0xFF1D9E75.toInt()
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(RectF(40f, 40f, widthF - 40f, 130f), 8f, 8f, headerPaint)

            val headerTextPaint = Paint().apply {
                color = Color.WHITE
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            canvas.drawText("InsuGO - Reporte de Glucemia", 60f, 82f, headerTextPaint)

            val headerSubTextPaint = Paint().apply {
                color = 0xD8FFFFFF.toInt() // White with 85% alpha
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            canvas.drawText("Monitoreo y control semanal de glucosa", 60f, 110f, headerSubTextPaint)

            // 2. Información del Paciente
            val infoLabelPaint = Paint().apply {
                color = 0xFF5F5E5A.toInt() // Gris600
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val infoValuePaint = Paint().apply {
                color = 0xFF2C2C2A.toInt() // Gris900
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }

            val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES")).format(Date())

            canvas.drawText("Paciente:", 60f, 170f, infoLabelPaint)
            canvas.drawText(nombreUsuario.ifBlank { "Usuario de InsuGO" }, 150f, 170f, infoValuePaint)

            canvas.drawText("Fecha reporte:", 60f, 190f, infoLabelPaint)
            canvas.drawText(formattedDate, 150f, 190f, infoValuePaint)

            canvas.drawText("Rango:", 60f, 210f, infoLabelPaint)
            canvas.drawText("Últimos 7 días", 150f, 210f, infoValuePaint)

            // Línea divisoria
            val linePaint = Paint().apply {
                color = 0xFFD3D1C7.toInt() // Gris200
                strokeWidth = 1.5f
            }
            canvas.drawLine(40f, 230f, widthF - 40f, 230f, linePaint)

            // 3. Tarjetas de Estadísticas (Promedio y Mediciones)
            val cardWidth = (widthF - 90f) / 2f

            // Tarjeta 1: Promedio
            val box1Paint = Paint().apply {
                color = 0xFFEAF3DE.toInt() // VerdeClaroHist
                style = Paint.Style.FILL
            }
            val box1Rect = RectF(40f, 250f, 40f + cardWidth, 330f)
            canvas.drawRoundRect(box1Rect, 8f, 8f, box1Paint)

            val labelStatsPaint1 = Paint().apply {
                color = 0xFF27500A.toInt()
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            canvas.drawText("Promedio Glucemia", 55f, 275f, labelStatsPaint1)

            val valueStatsPaint1 = Paint().apply {
                color = 0xFF173404.toInt() // VerdeTextoHist
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            canvas.drawText("${state.promedio}", 55f, 310f, valueStatsPaint1)

            val unitStatsPaint1 = Paint().apply {
                color = 0xFF3B6D11.toInt()
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            canvas.drawText("mg/dL", 55f + valueStatsPaint1.measureText("${state.promedio}  "), 310f, unitStatsPaint1)

            // Tarjeta 2: Mediciones
            val box2Paint = Paint().apply {
                color = 0xFFFAEEDA.toInt() // AmbarClaroHist
                style = Paint.Style.FILL
            }
            val box2Rect = RectF(widthF - 40f - cardWidth, 250f, widthF - 40f, 330f)
            canvas.drawRoundRect(box2Rect, 8f, 8f, box2Paint)

            val labelStatsPaint2 = Paint().apply {
                color = 0xFF854F0B.toInt() // AmbarOscuro
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            canvas.drawText("Total Mediciones", widthF - 25f - cardWidth, 275f, labelStatsPaint2)

            val valueStatsPaint2 = Paint().apply {
                color = 0xFF412402.toInt() // AmbarTexto
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            canvas.drawText("${state.totalMediciones}", widthF - 25f - cardWidth, 310f, valueStatsPaint2)

            val unitStatsPaint2 = Paint().apply {
                color = 0xFF854F0B.toInt()
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            canvas.drawText("registros", widthF - 25f - cardWidth + valueStatsPaint2.measureText("${state.totalMediciones}  "), 310f, unitStatsPaint2)

            // 4. Gráfico
            val graphTitlePaint = Paint().apply {
                color = 0xFF2C2C2A.toInt() // Gris900
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            canvas.drawText("Historial de Glucemia Diaria (Tendencia)", 40f, 365f, graphTitlePaint)

            val graphBoxPaint = Paint().apply {
                color = 0xFFFFFFFF.toInt()
                style = Paint.Style.FILL
            }
            val graphBoxRect = RectF(40f, 385f, widthF - 40f, 565f)
            canvas.drawRoundRect(graphBoxRect, 10f, 10f, graphBoxPaint)

            val graphBorderPaint = Paint().apply {
                color = 0xFFD3D1C7.toInt() // Gris200
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            canvas.drawRoundRect(graphBoxRect, 10f, 10f, graphBorderPaint)

            // Barras del Gráfico
            val numBars = state.barras.size
            if (numBars > 0) {
                val graphLeft = 60f
                val graphRight = widthF - 60f
                val graphBottom = 525f
                val graphTop = 405f
                val maxGraphHeight = graphBottom - graphTop // 120f

                val availableWidth = graphRight - graphLeft
                val barSpacing = 16f
                val totalSpacing = barSpacing * (numBars - 1)
                val individualBarWidth = (availableWidth - totalSpacing) / numBars

                val labelDayPaint = Paint().apply {
                    color = 0xFF5F5E5A.toInt() // Gris600
                    textSize = 11f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }

                state.barras.forEachIndexed { index, barra ->
                    val barHeight = barra.altura * maxGraphHeight
                    val barLeft = graphLeft + index * (individualBarWidth + barSpacing)
                    val barRight = barLeft + individualBarWidth
                    val barTop = graphBottom - barHeight

                    val barColor = when {
                        barra.altura >= 0.85f -> 0xFFE24B4A.toInt() // RojoHist
                        barra.altura >= 0.70f -> 0xFFBA7517.toInt() // Ambar
                        else -> 0xFF1D9E75.toInt() // VerdeHist
                    }

                    val barPaint = Paint().apply {
                        color = barColor
                        style = Paint.Style.FILL
                    }

                    val barRect = RectF(barLeft, barTop, barRight, graphBottom)
                    canvas.drawRoundRect(barRect, 4f, 4f, barPaint)

                    val labelX = barLeft + (individualBarWidth / 2f)
                    canvas.drawText(barra.dia, labelX, graphBottom + 20f, labelDayPaint)
                }
            }

            // Leyendas del gráfico
            val legendY = 590f
            val legendPaint = Paint().apply {
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }

            val dotPaintVerde = Paint().apply { color = 0xFF1D9E75.toInt(); style = Paint.Style.FILL }
            canvas.drawRoundRect(RectF(40f, legendY - 8f, 50f, legendY), 2f, 2f, dotPaintVerde)
            legendPaint.color = 0xFF5F5E5A.toInt()
            canvas.drawText("En rango", 55f, legendY, legendPaint)

            val dotPaintAmbar = Paint().apply { color = 0xFFBA7517.toInt(); style = Paint.Style.FILL }
            canvas.drawRoundRect(RectF(120f, legendY - 8f, 130f, legendY), 2f, 2f, dotPaintAmbar)
            canvas.drawText("Atención", 135f, legendY, legendPaint)

            val dotPaintRojo = Paint().apply { color = 0xFFE24B4A.toInt(); style = Paint.Style.FILL }
            canvas.drawRoundRect(RectF(200f, legendY - 8f, 210f, legendY), 2f, 2f, dotPaintRojo)
            canvas.drawText("Alto", 215f, legendY, legendPaint)

            // 5. Caja de Observación General
            val obsBoxPaint = Paint().apply {
                color = 0xFFFAEEDA.toInt() // AmbarClaroHist
                style = Paint.Style.FILL
            }
            val obsBoxRect = RectF(40f, 620f, widthF - 40f, 710f)
            canvas.drawRoundRect(obsBoxRect, 8f, 8f, obsBoxPaint)

            val obsTitlePaint = Paint().apply {
                color = 0xFF854F0B.toInt() // AmbarOscuro
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            canvas.drawText("Observación general del período", 55f, 645f, obsTitlePaint)

            val obsTextPaint = Paint().apply {
                color = 0xFF412402.toInt() // AmbarTexto
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            val obsMessage = if (state.promedio >= 140) {
                "El promedio semanal está elevado (${state.promedio} mg/dL). Te sugerimos contactar a tu especialista."
            } else {
                "El promedio de glucemia semanal está dentro del rango esperado. Mantén tus hábitos saludables."
            }
            canvas.drawText(obsMessage, 55f, 670f, obsTextPaint)
            canvas.drawText("Revisa tus comidas y mantén el registro constante en InsuGO.", 55f, 690f, obsTextPaint)

            // 6. Pie de página
            val footerPaint = Paint().apply {
                color = 0xFF5F5E5A.toInt() // Gris600
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("Reporte generado por InsuGO. Información para uso médico.", widthF / 2f, 750f, footerPaint)
            canvas.drawText("Este documento es orientativo y no reemplaza la consulta médica.", widthF / 2f, 765f, footerPaint)
        }
    }
}
