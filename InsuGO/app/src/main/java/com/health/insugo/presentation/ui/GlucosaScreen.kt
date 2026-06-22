package com.health.insugo.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.health.insugo.presentation.viewmodel.GlucosaViewModel
import org.koin.androidx.compose.koinViewModel

// Paleta de colores profesional
private val VerdeG = Color(0xFF1D9E75)
private val NaranjaG = Color(0xFFD85A30)
private val VerdeClaroG = Color(0xFFEAF3DE)
private val VerdeTextoG = Color(0xFF173404)
private val FondoG = Color(0xFFF1EFE8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlucosaScreen(
    onGuardar: () -> Unit,
    onVolver: () -> Unit,
    viewModel: GlucosaViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val valorGlucosa by viewModel.valorGlucosa.collectAsState()
    val errorMensaje by viewModel.errorMensaje.collectAsState()

    var momentoSeleccionado by remember { mutableStateOf("En ayunas") }
    var estaGuardando by remember { mutableStateOf(false) }

    val momentos = listOf("En ayunas", "Después de comer", "Antes de dormir", "Otro")

    @Composable
    fun TecladoNumerico(onTeclaPresionada: (String) -> Unit) {
        val teclas = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "‹")
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            teclas.forEach { fila ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    fila.forEach { tecla ->
                        Button(
                            onClick = { onTeclaPresionada(tecla) }, // Aquí pasamos el String
                            modifier = Modifier.weight(1f).height(60.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8E4D6))
                        ) {
                            Text(tecla, fontSize = 22.sp, color = Color.Black)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(errorMensaje) {
        errorMensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Anotar glucosa", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VerdeG, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    estaGuardando = true
                    viewModel.guardarMedicion(momentoSeleccionado) {
                        estaGuardando = false
                        onGuardar()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaG),
                enabled = valorGlucosa.isNotEmpty() && valorGlucosa != "0" && !estaGuardando
            ) {
                if (estaGuardando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar medición", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = FondoG
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Display valor
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = VerdeClaroG,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Valor actual", fontSize = 14.sp, color = VerdeTextoG.copy(alpha = 0.8f))
                    Text(
                        text = "${valorGlucosa.ifEmpty { "0" }} mg/dL",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeTextoG
                    )
                }
            }

            // Selección de momento
            Text("¿Cuándo fue la medición?", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                momentos.chunked(2).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        fila.forEach { momento ->
                            val esSel = momentoSeleccionado == momento
                            OutlinedButton(
                                onClick = { momentoSeleccionado = momento },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (esSel) VerdeG else Color.Transparent
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(if (esSel) VerdeG else Color.Gray)
                                )
                            ) {
                                Text(momento, color = if (esSel) Color.White else Color.Gray)
                            }
                        }
                    }
                }
            }

            // Teclado (Simplificado)
            TecladoNumerico(onTeclaPresionada = { tecla: String -> // <-- Agregamos : String
                when (tecla) {
                    "C" -> viewModel.borrarDigito()
                    "‹" -> viewModel.borrarDigito()
                    else -> viewModel.agregarDigito(tecla)
                }
            })
        }
    }

}