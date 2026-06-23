package com.health.insugo.presentation.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.health.insugo.R
import com.health.insugo.presentation.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private val Verde = Color(0xFF1D9E75)
private val Naranja = Color(0xFFD85A30)
private val Gris600 = Color(0xFF5F5E5A)
private val Gris200 = Color(0xFFD3D1C7)
private val Gris900 = Color(0xFF2C2C2A)

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
    // 🔌 Inyectamos el ViewModel automáticamente con Koin
    viewModel: AuthViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Cambiamos el booleano por un String para mostrar el error real de Firebase
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // Header verde (Intacto, está genial)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Verde)
                .padding(top = 48.dp, bottom = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Naranja)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text("InsuGO", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Medium)
                Text("Tu compañero diario", color = Color.White.copy(alpha = 0.95f), fontSize = 16.sp)
            }
        }

        // Formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1EFE8))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Hola, ingresá", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = Gris900)
            Text("Bienvenido de vuelta", fontSize = 16.sp, color = Gris600)

            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it; errorMsg = null },
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

            // Mostramos el mensaje de error si existe
            if (errorMsg != null) {
                Text(
                    text = errorMsg ?: "Error de autenticación",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    if (usuario.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        errorMsg = null
                        // 🚀 Llamamos a Firebase!
                        viewModel.iniciarSesion(usuario, password) { exito, error ->
                            isLoading = false
                            if (exito) {
                                onLoginExitoso()
                            } else {
                                errorMsg = error ?: "Credenciales inválidas"
                            }
                        }
                    } else {
                        errorMsg = "Por favor, completá todos los campos"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Naranja),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Ingresar", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Gris200)
                Text("  o  ", fontSize = 14.sp, color = Color(0xFF888780))
                HorizontalDivider(modifier = Modifier.weight(1f), color = Gris200)
            }

            OutlinedButton(
                onClick = {
                    errorMsg = null
                    isLoading = true
                    // 🚀 Levantamos el cartelito de Google en una corrutina
                    coroutineScope.launch {
                        iniciarConGoogle(context, viewModel, { isLoading = false; onLoginExitoso() }) { error ->
                            isLoading = false
                            errorMsg = error
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Gris600, modifier = Modifier.size(22.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4285F4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Continuar con Google", fontSize = 16.sp, color = Gris900)
                }
            }

            TextButton(
                onClick = onIrARegistro,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("¿Primera vez? Registrate", color = Verde, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// Lógica pesada de Google Sign-In separada de la vista principal
private suspend fun iniciarConGoogle(
    context: Context,
    viewModel: AuthViewModel,
    onLoginExitoso: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            request = request,
            context = context,
        )

        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            viewModel.iniciarSesionConGoogle(googleIdTokenCredential.idToken) { exito, errorMsg ->
                if (exito) {
                    onLoginExitoso()
                } else {
                    onError(errorMsg ?: "Fallo al validar con Firebase")
                }
            }
        }
    } catch (e: Exception) {
        // Esto atrapa, por ejemplo, si el usuario cierra la ventanita de Google sin elegir
        // cuenta. Mostramos el mensaje real (no uno genérico) para poder diagnosticar
        // problemas de configuración como un SHA-1 no registrado en Firebase.
        onError(e.message ?: "Inicio de sesión cancelado o fallido.")
    }
}