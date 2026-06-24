package com.health.insugo.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.health.insugo.presentation.viewmodel.HistorialViewModel
import org.koin.androidx.compose.koinViewModel

private val VerdeHist = Color(0xFF1D9E75)
private val NaranjaHist = Color(0xFFD85A30)
private val VerdeClaroHist = Color(0xFFEAF3DE)
private val VerdeTextoHist = Color(0xFF173404)
private val AmbarClaroHist = Color(0xFFFAEEDA)
private val AmbarOscuroHist = Color(0xFF854F0B)
private val AmbarTextoHist = Color(0xFF412402)
private val RojoHist = Color(0xFFE24B4A)
private val Gris200Hist = Color(0xFFD3D1C7)
private val Gris600Hist = Color(0xFF5F5E5A)
private val Gris900Hist = Color(0xFF2C2C2A)
private val FondoHist = Color(0xFFF1EFE8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onCompartir: () -> Unit,
    onVolver: () -> Unit,
    viewModel: HistorialViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu semana", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = onCompartir) {
                        Text(
                            "Compartir",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeHist,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = FondoHist
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Resumen stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = VerdeClaroHist
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Promedio", fontSize = 13.sp, color = Color(0xFF27500A))
                        Text("${uiState.promedio}", fontSize = 26.sp, fontWeight = FontWeight.Medium, color = VerdeTextoHist)
                        Text("mg/dL", fontSize = 12.sp, color = Color(0xFF3B6D11))
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = AmbarClaroHist
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Mediciones", fontSize = 13.sp, color = AmbarOscuroHist)
                        Text("${uiState.totalMediciones}", fontSize = 26.sp, fontWeight = FontWeight.Medium, color = AmbarTextoHist)
                        Text("esta semana", fontSize = 12.sp, color = AmbarOscuroHist)
                    }
                }
            }

            // Gráfico
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Glucemia diaria", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Gris900Hist)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Barras
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.barras.forEach { barra ->
                            val color = when {
                                barra.altura >= 0.85f -> RojoHist
                                barra.altura >= 0.70f -> Color(0xFFBA7517)
                                else -> VerdeHist
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(barra.altura)
                                    .background(color, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            )
                        }
                    }

                    // Labels días
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.barras.forEach { barra ->
                            Text(
                                barra.dia,
                                modifier = Modifier.weight(1f),
                                fontSize = 13.sp,
                                color = Gris600Hist,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE8E4D6))

                    // Referencia colores
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        listOf(
                            VerdeHist to "En rango",
                            Color(0xFFBA7517) to "Atención",
                            RojoHist to "Alto"
                        ).forEach { (color, label) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(label, fontSize = 13.sp, color = Gris600Hist)
                            }
                        }
                    }
                }
            }

            // Tabla de últimas mediciones
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Últimas mediciones", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Gris900Hist)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.mediciones.isEmpty()) {
                        Text(
                            "No hay mediciones registradas.",
                            fontSize = 14.sp,
                            color = Gris600Hist,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        // Header de la tabla
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(FondoHist, RoundedCornerShape(8.dp))
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fecha / Hora", modifier = Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Gris600Hist)
                            Text("Momento", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Gris600Hist)
                            Text("Valor", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Gris600Hist, textAlign = androidx.compose.ui.text.style.TextAlign.End)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Filas de datos
                        uiState.mediciones.forEachIndexed { index, medicion ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val sdf = java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale("es", "ES"))
                                val fechaFormateada = sdf.format(medicion.fecha)
                                
                                Text(fechaFormateada, modifier = Modifier.weight(2f), fontSize = 14.sp, color = Gris900Hist)
                                Text(medicion.momentoDia, modifier = Modifier.weight(1.5f), fontSize = 14.sp, color = Gris600Hist)

                                val textColor = when {
                                    medicion.valor >= 170 -> RojoHist
                                    medicion.valor >= 140 -> Color(0xFFBA7517)
                                    else -> VerdeHist
                                }
                                Text(
                                    "${medicion.valor} mg/dL",
                                    modifier = Modifier.weight(1f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                                )
                            }
                            if (index < uiState.mediciones.size - 1) {
                                HorizontalDivider(color = Color(0xFFE8E4D6), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

            // Observación
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = AmbarClaroHist,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lo que vimos", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AmbarOscuroHist)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "El viernes tu glucosa subió. Ese día comiste pastas y no caminaste.",
                        fontSize = 16.sp, color = AmbarTextoHist, lineHeight = 24.sp
                    )
                }
            }

            Button(
                onClick = onCompartir,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaHist)
            ) {
                Text("Compartir con mi médico", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
