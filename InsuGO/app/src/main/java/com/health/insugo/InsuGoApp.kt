package com.health.insugo

import android.app.Application
import com.health.insugo.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class InsuGoApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // ¡Acá encendemos el motor de Koin!
        startKoin {
            // Muestra errores de Koin en el Logcat si algo falla
            androidLogger()
            // Le pasamos el contexto de la app
            androidContext(this@InsuGoApp)
            // Le cargamos tu archivo de configuración
            modules(appModule)
        }
    }
}