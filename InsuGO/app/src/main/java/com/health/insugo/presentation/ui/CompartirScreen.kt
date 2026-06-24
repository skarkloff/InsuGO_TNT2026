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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.health.insugo.presentation.viewmodel.HistorialViewModel
import org.koin.androidx.compose.koinViewModel

private val VerdeComp = Color(0xFF1D9E75)
private val RojoComp = Color(0xFFE24B4A)
private val Gris600Comp = Color(0xFF5F5E5A)
private val Gris900Comp = Color(0xFF2C2C2A)
private val FondoComp = Color(0xFFF1EFE8)
private val WhatsApp = Color(0xFF25D366)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompartirScreen(
    onVolver: () -> Unit,
    viewModel: HistorialViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compartir reporte", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeComp,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = FondoComp
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                "Armamos un resumen de tu última semana para que se lo muestres a tu médico.",
                fontSize = 17.sp, color = Gris900Comp, lineHeight = 24.sp
            )

            // Tarjeta de reporte
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        "REPORTE SEMANAL",
                        fontSize = 13.sp,
                        color = Gris600Comp,
                        letterSpacing = androidx.compose.ui.unit.TextUnit(0.5f, androidx.compose.ui.unit.TextUnitType.Sp)
                    )
                    Text(
                        "${uiState.nombre.ifBlank { "Paciente" }} · Últimos 7 días",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gris900Comp
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE8E4D6))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Promedio glucemia", fontSize = 12.sp, color = Gris600Comp)
                            Text("${uiState.promedio} mg/dL", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Gris900Comp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mediciones", fontSize = 12.sp, color = Gris600Comp)
                            Text("${uiState.totalMediciones}", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Gris900Comp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mín / Máx", fontSize = 12.sp, color = Gris600Comp)
                            Text("98 / 178", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Gris900Comp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Min. movimiento", fontSize = 12.sp, color = Gris600Comp)
                            Text("145", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Gris900Comp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mini gráfico dinámico
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color(0xFFF8F6EE), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        uiState.barras.forEach { barra ->
                            val color = when {
                                barra.altura >= 0.85f -> RojoComp
                                barra.altura >= 0.70f -> Color(0xFFBA7517)
                                else -> VerdeComp
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(barra.altura)
                                    .background(color, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }

            Text("¿Cómo querés compartirlo?", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Gris900Comp)

            // Opciones de compartir
            Button(
                onClick = { viewModel.compartirPorWhatsApp(context) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WhatsApp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color.White.copy(alpha = 0.3f), shape = RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Enviar por WhatsApp", fontSize = 17.sp, fontWeight = FontWeight.Medium)
            }

            OutlinedButton(
                onClick = { viewModel.exportarACompPDF(context) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Descargar PDF", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Gris900Comp)
            }

            OutlinedButton(
                onClick = { /* Pantalla completa — próximamente */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Mostrar en pantalla", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Gris900Comp)
            }
        }
    }
}
