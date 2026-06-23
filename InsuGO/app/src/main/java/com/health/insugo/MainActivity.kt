package com.health.insugo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.health.insugo.presentation.AuthViewModel
import com.health.insugo.presentation.navigation.InsuGoNavGraph
import com.health.insugo.presentation.ui.LoginScreen
import com.health.insugo.presentation.ui.RegistroScreen
import com.health.insugo.ui.theme.InsuGOTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🧹 Borramos el startKoin de acá porque InsuGoApp ya lo hace al abrir la app.

        enableEdgeToEdge()

        setContent {
            InsuGOTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos a nuestro semáforo inteligente
                    EnrutadorPrincipal()
                }
            }
        }
    }
}

@Composable
fun EnrutadorPrincipal(
    viewModel: AuthViewModel = koinViewModel()
) {
    // Observamos en tiempo real si hay un usuario conectado en Firebase
    val usuarioActual by viewModel.usuarioActual.collectAsState()
    var destinoInicial by remember { mutableStateOf("HOME") }
    var mostrarRegistro by remember { mutableStateOf(false) }

    if (usuarioActual == null) {
        if (mostrarRegistro) {
            RegistroScreen(
                onRegistroExitoso = { destinoInicial = "HOME" },
                onVolver = { mostrarRegistro = false }
            )
        } else {
            LoginScreen(
                onLoginExitoso = { destinoInicial = "HOME" },
                onIrARegistro = { mostrarRegistro = true }
            )
        }
    } else {
        // 🟢 Hay usuario: ¡Encendemos tu sistema de navegación!
        InsuGoNavGraph(startDestination = destinoInicial)
    }
}