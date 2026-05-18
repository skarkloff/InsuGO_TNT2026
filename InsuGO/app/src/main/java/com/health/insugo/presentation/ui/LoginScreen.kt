package com.health.insugo.presentation.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Verde = Color(0xFF1D9E75)
private val Naranja = Color(0xFFD85A30)
private val Gris600 = Color(0xFF5F5E5A)
private val Gris200 = Color(0xFFD3D1C7)
private val Gris900 = Color(0xFF2C2C2A)

// Credenciales hardcodeadas (temporario hasta implementar auth real)
private const val USUARIO_VALIDO = "admin"
private const val PASSWORD_VALIDA = "1234"

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // Header verde
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
                onValueChange = { usuario = it; error = false },
                label = { Text("Correo o DNI") },
                placeholder = { Text("tucorreo@ejemplo.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = error
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = false },
                label = { Text("Contraseña") },
                placeholder = { Text("••••••••") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = error
            )

            if (error) {
                Text(
                    "Usuario o contraseña incorrectos",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    if (usuario == USUARIO_VALIDO && password == PASSWORD_VALIDA) {
                        onLoginExitoso()
                    } else {
                        error = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Naranja)
            ) {
                Text("Ingresar", fontSize = 18.sp, fontWeight = FontWeight.Medium)
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
                onClick = { /* Google Auth — próximamente */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
            ) {
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

            TextButton(
                onClick = onIrARegistro,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Primera vez? Registrate", color = Verde, fontWeight = FontWeight.Medium)
            }
        }
    }
}
