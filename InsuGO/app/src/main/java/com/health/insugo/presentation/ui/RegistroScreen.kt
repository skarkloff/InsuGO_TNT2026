package com.health.insugo.presentation.ui

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.health.insugo.presentation.AuthViewModel
import com.health.insugo.presentation.viewmodel.PerfilViewModel
import org.koin.androidx.compose.koinViewModel

private val VerdeR = Color(0xFF1D9E75)
private val NaranjaR = Color(0xFFD85A30)
private val VerdeClaroR = Color(0xFFEAF3DE)
private val VerdeTextoR = Color(0xFF173404)
private val Gris200R = Color(0xFFD3D1C7)
private val Gris600R = Color(0xFF5F5E5A)
private val Gris900R = Color(0xFF2C2C2A)
private val FondoR = Color(0xFFF1EFE8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel(),
    perfilViewModel: PerfilViewModel = koinViewModel()
) {
    val nombre by perfilViewModel.nombre.collectAsState()
    val diagnostico by perfilViewModel.diagnostico.collectAsState()
    val ubicacionActivada by perfilViewModel.ubicacionActivada.collectAsState()

    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val crearCuenta = {
        when {
            correo.isBlank() || password.isBlank() || confirmarPassword.isBlank() || nombre.isBlank() ->
                errorMsg = "Completá todos los campos para registrarte"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
                errorMsg = "Ingresá un correo electrónico válido"
            password.length < 6 ->
                errorMsg = "La contraseña debe tener al menos 6 caracteres"
            password.none { it.isDigit() } ->
                errorMsg = "La contraseña debe incluir al menos un número"
            password != confirmarPassword ->
                errorMsg = "Las contraseñas no coinciden"
            else -> {
                isLoading = true
                errorMsg = null
                authViewModel.registrarse(correo, password) { exito, error ->
                    if (exito) {
                        perfilViewModel.guardarPerfil {
                            isLoading = false
                            onRegistroExitoso()
                        }
                    } else {
                        isLoading = false
                        errorMsg = error ?: "No se pudo crear la cuenta"
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Creá tu cuenta", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeR,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = FondoR
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Contanos sobre vos", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = Gris900R)
            Text(
                "Con estos datos armamos tu cuenta y tus consejos a medida.",
                fontSize = 16.sp, color = Gris600R, lineHeight = 24.sp
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it; errorMsg = null },
                label = { Text("Correo electrónico") },
                placeholder = { Text("tucorreo@ejemplo.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = errorMsg != null
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMsg = null },
                label = { Text("Contraseña") },
                placeholder = { Text("••••••••") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = errorMsg != null
            )

            OutlinedTextField(
                value = confirmarPassword,
                onValueChange = { confirmarPassword = it; errorMsg = null },
                label = { Text("Repetir contraseña") },
                placeholder = { Text("••••••••") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = errorMsg != null
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { perfilViewModel.actualizarNombre(it); errorMsg = null },
                label = { Text("¿Cómo te llamás?") },
                placeholder = { Text("Luis") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = errorMsg != null
            )

            Text("¿Qué te diagnosticaron?", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Gris900R)

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Diabetes tipo 2", "Prediabetes").forEach { opcion ->
                    val seleccionado = diagnostico == opcion
                    Button(
                        onClick = { perfilViewModel.seleccionarDiagnostico(opcion) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (seleccionado) VerdeR else Color.White,
                            contentColor = if (seleccionado) Color.White else Gris600R
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
                color = VerdeClaroR,
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("¿Activamos tu ubicación?", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = VerdeTextoR)
                    Text(
                        "Para darte consejos según el clima y los alimentos de tu zona.",
                        fontSize = 15.sp, color = Color(0xFF27500A), lineHeight = 22.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { perfilViewModel.seleccionarUbicacionActivada(true) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (ubicacionActivada) VerdeR else Color.White,
                                contentColor = if (ubicacionActivada) Color.White else Gris600R
                            ),
                            border = if (!ubicacionActivada) ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp) else null
                        ) { Text("Sí, activar", fontSize = 16.sp, fontWeight = FontWeight.Medium) }

                        Button(
                            onClick = { perfilViewModel.seleccionarUbicacionActivada(false) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!ubicacionActivada) VerdeR else Color.White,
                                contentColor = if (!ubicacionActivada) Color.White else Gris600R
                            ),
                            border = if (ubicacionActivada) ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp) else null
                        ) { Text("Ahora no", fontSize = 16.sp, fontWeight = FontWeight.Medium) }
                    }
                }
            }

            if (errorMsg != null) {
                Text(
                    text = errorMsg ?: "Error al registrarte",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = crearCuenta,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaR),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Crear cuenta", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }

            TextButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("¿Ya tenés cuenta? Iniciá sesión", color = VerdeR, fontWeight = FontWeight.Medium)
            }
        }
    }
}
