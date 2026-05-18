package com.health.insugo.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeAc = Color(0xFF1D9E75)
private val VerdeOscuroAc = Color(0xFF0F6E56)
private val VerdeClaroAc = Color(0xFFEAF3DE)
private val NaranjaAc = Color(0xFFD85A30)
private val NaranjaClaroAc = Color(0xFFFAECE7)
private val AmbarClaroAc = Color(0xFFFAEEDA)
private val AmbarOscuroAc = Color(0xFF854F0B)
private val RojoOscuroAc = Color(0xFF791F1F)
private val RojoTextoAc = Color(0xFF501313)
private val RojoClaroAc = Color(0xFFFCEBEB)
private val Gris600Ac = Color(0xFF5F5E5A)
private val Gris900Ac = Color(0xFF2C2C2A)
private val FondoAc = Color(0xFFF1EFE8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcercaDeScreen(
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de InsuGO", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeAc,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = FondoAc
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Logo + nombre
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(VerdeAc),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NaranjaAc)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text("InsuGO", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = Gris900Ac)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Tu compañero digital para el manejo de la prediabetes y la diabetes tipo 2",
                    fontSize = 15.sp, color = Gris600Ac, lineHeight = 22.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Qué es InsuGO
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Box(modifier = Modifier.width(5.dp).height(IntrinsicSize.Max).background(VerdeAc))
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("¿Qué es InsuGO?", fontSize = 14.sp, color = VerdeOscuroAc, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Más que un anotador, InsuGO es un compañero digital que te acompaña en la transición hacia un nuevo estilo de vida, adaptando sus recomendaciones a tus resultados, tu entorno y tu contexto diario.",
                            fontSize = 16.sp, color = Gris900Ac, lineHeight = 26.sp
                        )
                    }
                }
            }

            Text("¿Qué podés hacer?", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Gris900Ac)

            // Features
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureItem(
                    fondoIcono = NaranjaClaroAc,
                    colorIcono = NaranjaAc,
                    titulo = "Registrar glucosa",
                    descripcion = "Anotá tus mediciones diarias y cruzalas con tus hábitos y el clima."
                )
                FeatureItem(
                    fondoIcono = VerdeClaroAc,
                    colorIcono = VerdeOscuroAc,
                    titulo = "Consejos personalizados",
                    descripcion = "Nutrición estacional, actividad física según el clima patagónico, cuidado de pies y más."
                )
                FeatureItem(
                    fondoIcono = AmbarClaroAc,
                    colorIcono = AmbarOscuroAc,
                    titulo = "Historial visual",
                    descripcion = "Compartí un resumen semanal con tu médico para ajustar el tratamiento con datos reales."
                )
            }

            // Enfoque regional
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = VerdeClaroAc,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Enfoque regional", fontSize = 14.sp, color = VerdeOscuroAc, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Los consejos tienen en cuenta el clima patagónico, la disponibilidad de alimentos locales y las alertas sanitarias de tu zona.",
                        fontSize = 15.sp, color = Color(0xFF27500A), lineHeight = 22.sp
                    )
                }
            }

            // Disclaimer
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = RojoClaroAc,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Importante", fontSize = 14.sp, color = RojoOscuroAc, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "InsuGO no reemplaza la consulta médica. Siempre seguí las indicaciones de tu equipo de salud.",
                        fontSize = 15.sp, color = RojoTextoAc, lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    fondoIcono: Color,
    colorIcono: Color,
    titulo: String,
    descripcion: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(fondoIcono),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .then(Modifier.background(colorIcono, CircleShape))
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2C2C2A))
                Spacer(modifier = Modifier.height(4.dp))
                Text(descripcion, fontSize = 14.sp, color = Color(0xFF5F5E5A), lineHeight = 20.sp)
            }
        }
    }
}
