package com.health.insugo.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeCom = Color(0xFF1D9E75)
private val NaranjaCom = Color(0xFFD85A30)
private val VerdeClaroCom = Color(0xFFEAF3DE)
private val VerdeTextoCom = Color(0xFF173404)
private val Gris200Com = Color(0xFFD3D1C7)
private val Gris600Com = Color(0xFF5F5E5A)
private val Gris900Com = Color(0xFF2C2C2A)
private val FondoCom = Color(0xFFF1EFE8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComidaScreen(
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    var desayuno by remember { mutableStateOf("") }
    var almuerzo by remember { mutableStateOf("") }
    var cena by remember { mutableStateOf("") }
    var actividad by remember { mutableStateOf("Caminar") }
    var minutos by remember { mutableStateOf(30f) }
    val actividades = listOf("Caminar", "En casa", "Bici")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comida y movimiento", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeCom,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = FondoCom
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Anotá lo del día. No hace falta detalle, con frases cortas alcanza.",
                fontSize = 17.sp, color = Gris900Com, lineHeight = 24.sp
            )

            OutlinedTextField(
                value = desayuno,
                onValueChange = { desayuno = it },
                label = { Text("Desayuno") },
                placeholder = { Text("Ej: mate con tostadas") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = almuerzo,
                onValueChange = { almuerzo = it },
                label = { Text("Almuerzo") },
                placeholder = { Text("Ej: pollo con ensalada") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = cena,
                onValueChange = { cena = it },
                label = { Text("Cena") },
                placeholder = { Text("Ej: tortilla de zapallitos") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Actividad física
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = VerdeClaroCom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("¿Te moviste hoy?", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = VerdeTextoCom)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        actividades.forEach { act ->
                            val seleccionado = actividad == act
                            Button(
                                onClick = { actividad = act },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (seleccionado) VerdeCom else Color.White,
                                    contentColor = if (seleccionado) Color.White else Gris600Com
                                ),
                                border = if (!seleccionado) ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp) else null,
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Text(act, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Text(
                        "Minutos: ${minutos.toInt()}",
                        fontSize = 15.sp,
                        color = Color(0xFF27500A)
                    )
                    Slider(
                        value = minutos,
                        onValueChange = { minutos = it },
                        valueRange = 0f..120f,
                        colors = SliderDefaults.colors(
                            thumbColor = VerdeCom,
                            activeTrackColor = VerdeCom
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaCom)
            ) {
                Text("Guardar día", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
