package com.health.insugo.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.health.insugo.data.repository.AuthRepositoryFirebaseImpl
import com.health.insugo.data.repository.ComidaRepositoryFirebaseImpl
import com.health.insugo.data.repository.GlucosaRepositoryFirebaseImpl
import com.health.insugo.data.repository.PerfilRepositoryFirebaseImpl
import com.health.insugo.domain.AuthRepository
import com.health.insugo.domain.repository.ComidaRepository
import com.health.insugo.domain.repository.GlucosaRepository
import com.health.insugo.domain.repository.PerfilRepository
import com.health.insugo.presentation.AuthViewModel
import com.health.insugo.presentation.viewmodel.ComidaViewModel
import com.health.insugo.presentation.viewmodel.GlucosaViewModel
import com.health.insugo.presentation.viewmodel.HistorialViewModel
import com.health.insugo.presentation.viewmodel.HomeViewModel
import com.health.insugo.presentation.viewmodel.PerfilViewModel
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
    single<ComidaRepository> { ComidaRepositoryFirebaseImpl(firestore = get(), auth = get()) }
    single<PerfilRepository> { PerfilRepositoryFirebaseImpl(firestore = get(), auth = get()) }

    // 3. Registramos el ViewModel inyectándole el repositorio
    viewModel { AuthViewModel(authRepository = get<AuthRepository>()) }
    viewModel { GlucosaViewModel(repository = get()) }
    viewModel { ComidaViewModel(repository = get()) }
    viewModel { PerfilViewModel(repository = get()) }
    viewModel { HistorialViewModel(repository = get()) }
    viewModel { HomeViewModel(repository = get(), perfilRepository = get()) }
}