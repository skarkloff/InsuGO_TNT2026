package com.health.insugo.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.health.insugo.data.repository.AuthRepositoryFirebaseImpl
import com.health.insugo.data.repository.GlucosaRepositoryFirebaseImpl
import com.health.insugo.domain.AuthRepository
import com.health.insugo.domain.repository.GlucosaRepository
import com.health.insugo.presentation.AuthViewModel
import com.health.insugo.presentation.viewmodel.GlucosaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // 1. Le damos a Koin la instancia única de FirebaseAuth
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }


    single { com.google.firebase.firestore.FirebaseFirestore.getInstance() }
    // Le pasamos la base de datos y la autenticación al obrero de la glucosa
    single {
        GlucosaRepositoryFirebaseImpl(
            firestore = get(),
            auth = get()
        )
    }
    single<GlucosaRepository> {
        GlucosaRepositoryFirebaseImpl(firestore = get(), auth = get())
    }
    // 2. Unimos el contrato (domain) con el obrero de Firebase (data)
    single<AuthRepository> { AuthRepositoryFirebaseImpl(firebaseAuth = get()) }

    // 3. Registramos el ViewModel inyectándole el repositorio
    viewModel { AuthViewModel(authRepository = get<AuthRepository>()) }
    viewModel { GlucosaViewModel(repository = get()) }
}