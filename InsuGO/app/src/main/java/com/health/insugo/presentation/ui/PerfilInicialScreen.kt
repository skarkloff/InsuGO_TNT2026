package com.health.insugo.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeP = Color(0xFF1D9E75)
private val NaranjaP = Color(0xFFD85A30)
private val VerdeClaroP = Color(0xFFEAF3DE)
private val VerdeTextoP = Color(0xFF173404)
private val Gris200P = Color(0xFFD3D1C7)
private val Gris600P = Color(0xFF5F5E5A)
private val Gris900P = Color(0xFF2C2C2A)
private val FondoP = Color(0xFFF1EFE8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilInicialScreen(
    onContinuar: () -> Unit,
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var diagnostico by remember { mutableStateOf("Diabetes tipo 2") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurá tu perfil", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeP,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = FondoP
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Barra de progreso
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.weight(1f).height(6.dp).background(VerdeP, RoundedCornerShape(3.dp)))
                Box(modifier = Modifier.weight(1f).height(6.dp).background(VerdeP, RoundedCornerShape(3.dp)))
                Box(modifier = Modifier.weight(1f).height(6.dp).background(Gris200P, RoundedCornerShape(3.dp)))
            }
            Text("Paso 2 de 3", fontSize = 14.sp, color = Gris600P)

            Text("Contanos sobre vos", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = Gris900P)
            Text(
                "Esto nos ayuda a darte consejos justos para vos.",
                fontSize = 16.sp, color = Gris600P, lineHeight = 24.sp
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("¿Cómo te llamás?") },
                placeholder = { Text("Luis") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Text("¿Qué te diagnosticaron?", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Gris900P)

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Diabetes tipo 2", "Prediabetes").forEach { opcion ->
                    val seleccionado = diagnostico == opcion
                    Button(
                        onClick = { diagnostico = opcion },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (seleccionado) VerdeP else Color.White,
                            contentColor = if (seleccionado) Color.White else Gris600P
                        ),
                        border = if (!seleccionado) ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp) else null
                    ) {
                        Text(opcion, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Card ubicación
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = VerdeClaroP,
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("¿Activamos tu ubicación?", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = VerdeTextoP)
                    Text(
                        "Para darte consejos según el clima y los alimentos de tu zona.",
                        fontSize = 15.sp, color = Color(0xFF27500A), lineHeight = 22.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onContinuar,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeP)
                        ) { Text("Sí, activar", fontSize = 16.sp, fontWeight = FontWeight.Medium) }

                        OutlinedButton(
                            onClick = onContinuar,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("Ahora no", fontSize = 16.sp, color = Gris600P) }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onContinuar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaP)
            ) {
                Text("Continuar", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
