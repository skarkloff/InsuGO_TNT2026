package com.health.insugo.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.health.insugo.presentation.AuthViewModel
import com.health.insugo.presentation.viewmodel.PerfilViewModel
import org.koin.androidx.compose.koinViewModel

private val VerdePf = Color(0xFF1D9E75)
private val NaranjaPf = Color(0xFFD85A30)
private val Gris200Pf = Color(0xFFD3D1C7)
private val Gris600Pf = Color(0xFF5F5E5A)
private val Gris900Pf = Color(0xFF2C2C2A)
private val FondoPf = Color(0xFFF1EFE8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onVolver: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel(),
    perfilViewModel: PerfilViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val usuario by authViewModel.usuarioActual.collectAsState()
    val nombre by perfilViewModel.nombre.collectAsState()
    val diagnostico by perfilViewModel.diagnostico.collectAsState()
    val errorPerfil by perfilViewModel.errorMensaje.collectAsState()

    LaunchedEffect(errorPerfil) {
        errorPerfil?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            perfilViewModel.limpiarError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VerdePf, titleContentColor = Color.White)
            )
        },
        containerColor = FondoPf
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SeccionCuenta(correoActual = usuario?.email ?: "", authViewModel = authViewModel, context = context)

            SeccionDatosDeSalud(
                nombre = nombre,
                diagnostico = diagnostico,
                onNombreChange = perfilViewModel::actualizarNombre,
                onDiagnosticoChange = perfilViewModel::seleccionarDiagnostico,
                onGuardar = {
                    perfilViewModel.guardarPerfil {
                        Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            SeccionUbicacion()
        }
    }
}

@Composable
private fun SeccionCuenta(correoActual: String, authViewModel: AuthViewModel, context: android.content.Context) {
    var mostrarCambioCorreo by remember { mutableStateOf(false) }
    var mostrarCambioClave by remember { mutableStateOf(false) }

    Surface(shape = RoundedCornerShape(14.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Cuenta", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Gris900Pf)

            // Correo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Correo", fontSize = 13.sp, color = Gris600Pf)
                    Text(correoActual, fontSize = 16.sp, color = Gris900Pf)
                }
                TextButton(onClick = { mostrarCambioCorreo = !mostrarCambioCorreo }) {
                    Text(if (mostrarCambioCorreo) "Cancelar" else "Cambiar", color = VerdePf)
                }
            }
            if (mostrarCambioCorreo) {
                FormularioCambioCorreo(
                    authViewModel = authViewModel,
                    onExito = {
                        mostrarCambioCorreo = false
                        Toast.makeText(
                            context,
                            "Te enviamos un correo de verificación. El cambio se aplica cuando lo confirmes.",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                )
            }

            HorizontalDivider(color = Gris200Pf)

            // Contraseña
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Contraseña", fontSize = 13.sp, color = Gris600Pf)
                    Text("••••••••", fontSize = 16.sp, color = Gris900Pf)
                }
                TextButton(onClick = { mostrarCambioClave = !mostrarCambioClave }) {
                    Text(if (mostrarCambioClave) "Cancelar" else "Cambiar", color = VerdePf)
                }
            }
            if (mostrarCambioClave) {
                FormularioCambioClave(
                    authViewModel = authViewModel,
                    onExito = {
                        mostrarCambioClave = false
                        Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                    },
                    onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                )
            }
        }
    }
}

@Composable
private fun FormularioCambioCorreo(
    authViewModel: AuthViewModel,
    onExito: () -> Unit,
    onError: (String) -> Unit
) {
    var nuevoEmail by remember { mutableStateOf("") }
    var claveActual by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = nuevoEmail,
            onValueChange = { nuevoEmail = it },
            label = { Text("Nuevo correo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = claveActual,
            onValueChange = { claveActual = it },
            label = { Text("Tu contraseña actual") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Button(
            onClick = {
                if (nuevoEmail.isBlank() || claveActual.isBlank()) {
                    onError("Completá el nuevo correo y tu contraseña actual")
                    return@Button
                }
                enviando = true
                authViewModel.reautenticar(claveActual) { ok, error ->
                    if (!ok) {
                        enviando = false
                        onError(error ?: "No se pudo verificar la contraseña")
                        return@reautenticar
                    }
                    authViewModel.actualizarEmail(nuevoEmail) { ok2, error2 ->
                        enviando = false
                        if (ok2) onExito() else onError(error2 ?: "No se pudo cambiar el correo")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdePf),
            enabled = !enviando
        ) {
            if (enviando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            else Text("Confirmar cambio de correo")
        }
    }
}

@Composable
private fun FormularioCambioClave(
    authViewModel: AuthViewModel,
    onExito: () -> Unit,
    onError: (String) -> Unit
) {
    var claveActual by remember { mutableStateOf("") }
    var nuevaClave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = claveActual,
            onValueChange = { claveActual = it },
            label = { Text("Contraseña actual") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = nuevaClave,
            onValueChange = { nuevaClave = it },
            label = { Text("Nueva contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = confirmarClave,
            onValueChange = { confirmarClave = it },
            label = { Text("Confirmar nueva contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Button(
            onClick = {
                if (claveActual.isBlank() || nuevaClave.isBlank()) {
                    onError("Completá tu contraseña actual y la nueva")
                    return@Button
                }
                if (nuevaClave != confirmarClave) {
                    onError("Las contraseñas nuevas no coinciden")
                    return@Button
                }
                enviando = true
                authViewModel.reautenticar(claveActual) { ok, error ->
                    if (!ok) {
                        enviando = false
                        onError(error ?: "No se pudo verificar la contraseña")
                        return@reautenticar
                    }
                    authViewModel.actualizarClave(nuevaClave) { ok2, error2 ->
                        enviando = false
                        if (ok2) onExito() else onError(error2 ?: "No se pudo cambiar la contraseña")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdePf),
            enabled = !enviando
        ) {
            if (enviando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            else Text("Confirmar cambio de contraseña")
        }
    }
}

@Composable
private fun SeccionDatosDeSalud(
    nombre: String,
    diagnostico: String,
    onNombreChange: (String) -> Unit,
    onDiagnosticoChange: (String) -> Unit,
    onGuardar: () -> Unit
) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Datos de salud", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Gris900Pf)

            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = { Text("¿Cómo te llamás?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Text("¿Qué te diagnosticaron?", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Gris900Pf)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Diabetes tipo 2", "Prediabetes").forEach { opcion ->
                    val seleccionado = diagnostico == opcion
                    Button(
                        onClick = { onDiagnosticoChange(opcion) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (seleccionado) VerdePf else Color.White,
                            contentColor = if (seleccionado) Color.White else Gris600Pf
                        ),
                        border = if (!seleccionado) ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp) else null
                    ) {
                        Text(opcion, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaPf)
            ) {
                Text("Guardar cambios", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun SeccionUbicacion() {
    Surface(shape = RoundedCornerShape(14.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Ubicación", fontSize = 17.sp, fontWeight = FontWeight.Medium, color = Gris900Pf)
            Text("Todavía no implementado", fontSize = 14.sp, color = Gris600Pf)
        }
    }
}
