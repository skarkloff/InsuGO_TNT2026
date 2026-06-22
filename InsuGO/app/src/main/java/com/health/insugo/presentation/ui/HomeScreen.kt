package com.health.insugo.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.health.insugo.presentation.AuthViewModel
import com.health.insugo.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

private val VerdeH = Color(0xFF1D9E75)
private val NaranjaH = Color(0xFFD85A30)
private val NaranjaOscuroH = Color(0xFF993C1D)
private val VerdeClaroH = Color(0xFFEAF3DE)
private val VerdeTextoH = Color(0xFF173404)
private val AmbarClaroH = Color(0xFFFAEEDA)
private val AmbarOscuroH = Color(0xFF854F0B)
private val AmbarTextoH = Color(0xFF412402)
private val Gris200H = Color(0xFFD3D1C7)
private val Gris600H = Color(0xFF5F5E5A)
private val Gris900H = Color(0xFF2C2C2A)
private val FondoH = Color(0xFFF1EFE8)

@Composable
fun HomeScreen(
    onIrAGlucosa: () -> Unit,
    onIrAComida: () -> Unit,
    onIrAConsejos: () -> Unit,
    onIrAHistorial: () -> Unit,
    onIrAAcerca: () -> Unit,
    onIrAPerfil: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarMenuUsuario by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(FondoH)) {

        // Header verde
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeH)
                .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 26.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Buen día", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
                    Text(
                        uiState.nombre.ifBlank { "Usuario" },
                        color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Medium
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                            .clickable { onIrAAcerca() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("?", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                    Box {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(NaranjaH)
                                .clickable { mostrarMenuUsuario = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                uiState.nombre.firstOrNull()?.uppercase() ?: "U",
                                color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium
                            )
                        }
                        DropdownMenu(
                            expanded = mostrarMenuUsuario,
                            onDismissRequest = { mostrarMenuUsuario = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Ver perfil") },
                                onClick = {
                                    mostrarMenuUsuario = false
                                    onIrAPerfil()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    mostrarMenuUsuario = false
                                    authViewModel.cerrarSesion()
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White.copy(alpha = 0.18f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Trelew · 9°C · Viento 65 km/h", // Renderizado nativo simple
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
        }

        // Contenido scrolleable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Última glucemia
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = VerdeClaroH,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Tu última glucemia", fontSize = 15.sp, color = Color(0xFF27500A), fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            uiState.valor?.toString() ?: "--",
                            fontSize = 48.sp, fontWeight = FontWeight.Medium, color = VerdeTextoH, lineHeight = 48.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("mg/dL", fontSize = 17.sp, color = Color(0xFF3B6D11), modifier = Modifier.padding(bottom = 6.dp))
                    }
                    Text(
                        if (uiState.valor != null) "${uiState.fechaTexto} · ${uiState.estado}" else "Sin mediciones todavía",
                        fontSize = 15.sp, color = Color(0xFF3B6D11)
                    )
                }
            }

            Text("¿Qué querés hacer?", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Gris900H)

            // Botones de acción
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onIrAGlucosa,
                    modifier = Modifier.weight(1f).height(100.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NaranjaH)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Anotar glucosa", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
                OutlinedButton(
                    onClick = onIrAComida,
                    modifier = Modifier.weight(1f).height(100.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NaranjaOscuroH)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .border(2.5.dp, NaranjaOscuroH, RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Comida y movimiento", fontSize = 16.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                    }
                }
            }

            // Consejo del día
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onIrAConsejos() },
                shape = RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp),
                color = AmbarClaroH
            ) {
                Row {
                    Box(modifier = Modifier.width(5.dp).heightIn(min = 80.dp).background(AmbarOscuroH))
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                        Text("Consejo del día", fontSize = 14.sp, color = AmbarOscuroH, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Hay viento fuerte. Mejor caminar adentro de casa.",
                            fontSize = 17.sp, color = AmbarTextoH, lineHeight = 24.sp
                        )
                    }
                }
            }

            // Ver semana
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onIrAHistorial() },
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Ver mi semana", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Gris900H)
                        Text("Promedio: ${uiState.promedioSemanal} mg/dL", fontSize = 14.sp, color = Gris600H)
                    }
                    Text("›", fontSize = 24.sp, color = Gris600H)
                }
            }
        }

        // Bottom navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Gris200H, shape = RoundedCornerShape(0.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem(label = "Inicio", activo = true, onClick = {})
            NavItem(label = "Anotar", activo = false, onClick = onIrAGlucosa)
            NavItem(label = "Consejos", activo = false, onClick = onIrAConsejos)
            NavItem(label = "Historial", activo = false, onClick = onIrAHistorial)
        }
    }
}

@Composable
private fun NavItem(label: String, activo: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(
                    if (activo) Color(0xFF1D9E75) else Color.Transparent,
                    RoundedCornerShape(6.dp)
                )
                .border(2.dp, if (activo) Color(0xFF1D9E75) else Color(0xFF888780), RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 13.sp,
            color = if (activo) Color(0xFF1D9E75) else Color(0xFF5F5E5A),
            fontWeight = if (activo) FontWeight.Medium else FontWeight.Normal
        )
    }
}