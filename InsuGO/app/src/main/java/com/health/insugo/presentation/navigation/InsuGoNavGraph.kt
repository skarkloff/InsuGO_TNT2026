package com.health.insugo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.health.insugo.presentation.ui.AcercaDeScreen
import com.health.insugo.presentation.ui.ComidaScreen
import com.health.insugo.presentation.ui.CompartirScreen
import com.health.insugo.presentation.ui.ConsejosScreen
import com.health.insugo.presentation.ui.GlucosaScreen
import com.health.insugo.presentation.ui.HistorialScreen
import com.health.insugo.presentation.ui.HomeScreen
import com.health.insugo.presentation.ui.LoginScreen
import com.health.insugo.presentation.ui.PerfilInicialScreen

object Rutas {
    const val LOGIN = "login"
    const val PERFIL = "perfil"
    const val HOME = "home"
    const val GLUCOSA = "glucosa"
    const val COMIDA = "comida"
    const val CONSEJOS = "consejos"
    const val HISTORIAL = "historial"
    const val COMPARTIR = "compartir"
    const val ACERCA = "acerca"
}

@Composable
fun InsuGoNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.LOGIN) {

        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(Rutas.PERFIL) }
            )
        }

        composable(Rutas.PERFIL) {
            PerfilInicialScreen(
                onContinuar = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.HOME) {
            HomeScreen(
                onIrAGlucosa = { navController.navigate(Rutas.GLUCOSA) },
                onIrAComida = { navController.navigate(Rutas.COMIDA) },
                onIrAConsejos = { navController.navigate(Rutas.CONSEJOS) },
                onIrAHistorial = { navController.navigate(Rutas.HISTORIAL) },
                onIrAAcerca = { navController.navigate(Rutas.ACERCA) }
            )
        }

        composable(Rutas.GLUCOSA) {
            GlucosaScreen(
                onGuardar = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.COMIDA) {
            ComidaScreen(
                onGuardar = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.CONSEJOS) {
            ConsejosScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.HISTORIAL) {
            HistorialScreen(
                onCompartir = { navController.navigate(Rutas.COMPARTIR) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.COMPARTIR) {
            CompartirScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.ACERCA) {
            AcercaDeScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
