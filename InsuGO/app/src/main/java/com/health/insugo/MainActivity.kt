package com.health.insugo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.health.insugo.presentation.navigation.InsuGoNavGraph
import com.health.insugo.ui.theme.InsuGOTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InsuGOTheme {
                InsuGoNavGraph()
            }
        }
    }
}
