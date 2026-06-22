package com.health.insugo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.health.insugo.presentation.ui.*

@Composable
fun InsuGoNavGraph(startDestination: String = "HOME") {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable("PERFIL_INICIAL") {
            PerfilInicialScreen(
                onContinuar = {
                    navController.navigate("HOME") {
                        popUpTo("PERFIL_INICIAL") { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable("HOME") {
            HomeScreen(
                onIrAGlucosa = { navController.navigate("GLUCOSA") },
                onIrAComida = { navController.navigate("COMIDA") },
                onIrAConsejos = { navController.navigate("CONSEJOS") },
                onIrAHistorial = { navController.navigate("HISTORIAL") },
                onIrAAcerca = { navController.navigate("ACERCA") },
                onIrAPerfil = { navController.navigate("PERFIL") }
            )
        }

        composable("PERFIL") {
            PerfilScreen(onVolver = { navController.popBackStack() })
        }

        composable("GLUCOSA") {
            GlucosaScreen(
                onGuardar = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }
        // Declaraciones reales con todos sus parámetros obligatorios asignados
        composable(route = "COMIDA") {
            ComidaScreen(
                onGuardar = { navController.popBackStack() },
                onVolver = { navController.popBackStack() } // Agregamos el parámetro que faltaba
            )
        }

        composable(route = "CONSEJOS") {
            ConsejosScreen(onVolver = { navController.popBackStack() })
        }

        composable(route = "HISTORIAL") {
            HistorialScreen(
                onVolver = { navController.popBackStack() },
                onCompartir = { navController.navigate("COMPARTIR") }
            )
        }

        composable(route = "ACERCA") {
            AcercaDeScreen(onVolver = { navController.popBackStack() })
        }
    }
}