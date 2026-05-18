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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeCons = Color(0xFF1D9E75)
private val VerdeOscuroCons = Color(0xFF0F6E56)
private val VerdeClaroCons = Color(0xFFEAF3DE)
private val AmbarOscuroCons = Color(0xFF854F0B)
private val AmbarTextoCons = Color(0xFF412402)
private val AzulCons = Color(0xFF378ADD)
private val AzulOscuroCons = Color(0xFF0C447C)
private val RojoCons = Color(0xFFE24B4A)
private val RojoOscuroCons = Color(0xFF791F1F)
private val RojoTextoCons = Color(0xFF501313)
private val RojoClaroCons = Color(0xFFFCEBEB)
private val Gris600Cons = Color(0xFF5F5E5A)
private val Gris900Cons = Color(0xFF2C2C2A)
private val FondoCons = Color(0xFFF1EFE8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsejosScreen(
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consejos para hoy", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeCons,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = FondoCons
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Trelew · 9°C · viento fuerte", fontSize = 15.sp, color = Gris600Cons)

            // Movimiento
            ConsejoCard(
                categoriaColor = Color(0xFFBA7517),
                categoria = "Movimiento de hoy",
                titulo = "Mejor adentro",
                descripcion = "Hay viento fuerte. Probá 20 minutos de estiramiento o caminar por la casa.",
                fondo = Color.White
            )

            // Comida
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Box(modifier = Modifier.width(5.dp).height(IntrinsicSize.Max).background(VerdeOscuroCons))
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Comida de la zona", fontSize = 14.sp, color = VerdeOscuroCons, fontWeight = FontWeight.Medium)
                        Text("Frutos rojos en la feria", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Gris900Cons)
                        Text(
                            "Calafate y rosa mosqueta están baratos esta semana. Bajos en azúcar y buenos para vos.",
                            fontSize = 16.sp, color = Gris900Cons, lineHeight = 24.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Calafate", "Acelga", "Espinaca").forEach { tag ->
                                Surface(shape = RoundedCornerShape(16.dp), color = VerdeClaroCons) {
                                    Text(
                                        tag,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 14.sp,
                                        color = Color(0xFF27500A)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Cuidado de pies
            ConsejoCard(
                categoriaColor = AzulOscuroCons,
                categoria = "Cuidado de tus pies",
                titulo = "Revisión semanal",
                descripcion = "Mirá si hay heridas, callos o piel seca. Después poné crema hidratante.",
                fondo = Color.White
            )

            // Alerta
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = RojoClaroCons,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Alerta en tu zona", fontSize = 14.sp, color = RojoOscuroCons, fontWeight = FontWeight.Medium)
                    Text("Brote de gripe en Trelew", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = RojoTextoCons)
                    Text(
                        "Lavate las manos seguido y evitá lugares cerrados con mucha gente.",
                        fontSize = 16.sp, color = RojoTextoCons, lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ConsejoCard(
    categoriaColor: Color,
    categoria: String,
    titulo: String,
    descripcion: String,
    fondo: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = fondo,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            Box(modifier = Modifier.width(5.dp).height(IntrinsicSize.Max).background(categoriaColor))
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(categoria, fontSize = 14.sp, color = categoriaColor, fontWeight = FontWeight.Medium)
                Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2C2C2A))
                Text(descripcion, fontSize = 16.sp, color = Color(0xFF2C2C2A), lineHeight = 24.sp)
            }
        }
    }
}
