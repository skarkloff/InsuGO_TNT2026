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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeComp = Color(0xFF1D9E75)
private val RojoComp = Color(0xFFE24B4A)
private val Gris200Comp = Color(0xFFD3D1C7)
private val Gris600Comp = Color(0xFF5F5E5A)
private val Gris900Comp = Color(0xFF2C2C2A)
private val FondoComp = Color(0xFFF1EFE8)
private val WhatsApp = Color(0xFF25D366)

private val barrasComp = listOf(0.50f, 0.75f, 0.45f, 0.60f, 0.90f, 0.55f, 0.50f)
private val coloresComp = listOf(
    Color(0xFF1D9E75), Color(0xFFBA7517), Color(0xFF1D9E75),
    Color(0xFF1D9E75), Color(0xFFE24B4A), Color(0xFF1D9E75), Color(0xFF1D9E75)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompartirScreen(
    onVolver: () -> Unit
) {
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
                    Text("Luis · 12 al 18 de abril", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Gris900Comp)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE8E4D6))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Promedio glucemia", fontSize = 12.sp, color = Gris600Comp)
                            Text("124 mg/dL", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Gris900Comp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mediciones", fontSize = 12.sp, color = Gris600Comp)
                            Text("12", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Gris900Comp)
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

                    // Mini gráfico
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color(0xFFF8F6EE), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        barrasComp.forEachIndexed { i, altura ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(altura)
                                    .background(coloresComp[i], RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }

            Text("¿Cómo querés compartirlo?", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Gris900Comp)

            // Opciones de compartir
            Button(
                onClick = { /* WhatsApp — próximamente */ },
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
                onClick = { /* PDF — próximamente */ },
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
