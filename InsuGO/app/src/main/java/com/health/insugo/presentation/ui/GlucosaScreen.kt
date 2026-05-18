package com.health.insugo.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeG = Color(0xFF1D9E75)
private val NaranjaG = Color(0xFFD85A30)
private val NaranjaOscuroG = Color(0xFF993C1D)
private val VerdeClaroG = Color(0xFFEAF3DE)
private val VerdeTextoG = Color(0xFF173404)
private val Gris200G = Color(0xFFD3D1C7)
private val Gris600G = Color(0xFF5F5E5A)
private val Gris900G = Color(0xFF2C2C2A)
private val FondoG = Color(0xFFF1EFE8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlucosaScreen(
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    var valorGlucosa by remember { mutableStateOf("118") }
    var momentoSeleccionado by remember { mutableStateOf("En ayunas") }
    val momentos = listOf("En ayunas", "Después de comer", "Antes de dormir", "Otro")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Anotar glucosa", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeG,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = FondoG
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Display de valor
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = VerdeClaroG,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tu medición", fontSize = 15.sp, color = Color(0xFF27500A), fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            valorGlucosa.ifEmpty { "0" },
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Medium,
                            color = VerdeTextoG,
                            lineHeight = 56.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("mg/dL", fontSize = 18.sp, color = Color(0xFF3B6D11), modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            }

            // Momento
            Text("¿Cuándo te la mediste?", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Gris900G)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                momentos.chunked(2).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fila.forEach { momento ->
                            val seleccionado = momentoSeleccionado == momento
                            Button(
                                onClick = { momentoSeleccionado = momento },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (seleccionado) VerdeG else Color.White,
                                    contentColor = if (seleccionado) Color.White else Gris600G
                                ),
                                border = if (!seleccionado) ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp) else null
                            ) {
                                Text(momento, fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                            }
                        }
                        if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Teclado numérico
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8F6EE),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("C", "0", "‹")
                    ).forEach { fila ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            fila.forEach { tecla ->
                                Button(
                                    onClick = {
                                        when (tecla) {
                                            "C" -> valorGlucosa = "0"
                                            "‹" -> valorGlucosa = if (valorGlucosa.length > 1) valorGlucosa.dropLast(1) else "0"
                                            else -> {
                                                val actual = if (valorGlucosa == "0") "" else valorGlucosa
                                                if (actual.length < 3) valorGlucosa = actual + tecla
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(64.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Gris200G,
                                        contentColor = when (tecla) {
                                            "C" -> NaranjaOscuroG
                                            "‹" -> Gris600G
                                            else -> Gris900G
                                        }
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(0.dp)
                                ) {
                                    Text(tecla, fontSize = 26.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaG)
            ) {
                Text("Guardar medición", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
