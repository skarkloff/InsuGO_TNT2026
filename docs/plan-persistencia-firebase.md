# Plan de persistencia con Firebase

> Reemplaza al plan anterior (Room + Hilt). El proyecto migró a **Firebase** (Auth + Firestore) y **Koin** como inyección de dependencias.

## Contexto actual

- Ya existen los paquetes `data/` y `domain/` (Clean Architecture parcial).
- DI con **Koin** (`di/appModule.kt`), sin Hilt.
- Persistencia con **Firestore**, sin base de datos local ni Room.
- `AuthRepository` + `AuthViewModel` y `GlucosaRepository` + `GlucosaViewModel` ya están implementados y conectados:
  - `LoginScreen` y `GlucosaScreen` usan `koinViewModel()`.
- `ComidaScreen`, `PerfilInicialScreen`, `HistorialScreen` y `HomeScreen` todavía guardan su estado en `remember {}` o muestran datos hardcodeados — no persisten nada.
- No hay capa `usecase/`: los ViewModels existentes llaman al repositorio directamente. Se mantiene ese estilo por consistencia.

---

## Qué falta persistir

| Modelo de dominio | Campos | Origen |
|---|---|---|
| `Comida` | `id`, `desayuno`, `almuerzo`, `cena`, `actividad`, `minutosActividad`, `fecha` | `ComidaScreen.kt` (comida y actividad van juntas, mismo formulario de "día") |
| `Perfil` | `id` (= uid del usuario), `nombre`, `diagnostico` | `PerfilInicialScreen.kt` |

`RegistroGlucosa` y `Usuario` ya existen en `domain/model/` y no cambian.

---

## Patrón a seguir (igual que Glucosa)

No hay clases `Entity` ni DAOs. El repositorio mapea a mano `Map<String, Any>` ↔ modelo de dominio, igual que `GlucosaRepositoryFirebaseImpl`:

```
domain/
  model/
    Comida.kt
    Perfil.kt
  repository/
    ComidaRepository.kt      ← interface
    PerfilRepository.kt      ← interface
data/
  repository/
    ComidaRepositoryFirebaseImpl.kt
    PerfilRepositoryFirebaseImpl.kt
```

### Estructura en Firestore

- `usuarios/{uid}/comidas/{comidaId}` — una colección por usuario, igual que `usuarios/{uid}/registros` para glucosa.
- `usuarios/{uid}` (documento raíz del usuario) — guarda `nombre` y `diagnostico` con `set(merge = true)`, ya que el perfil es 1:1 con el usuario (no necesita subcolección).

### Interfaces de dominio

```kotlin
interface ComidaRepository {
    suspend fun guardarComida(comida: Comida): Result<Unit>
    fun obtenerComidasDelDia(fecha: Date): Flow<List<Comida>>
}

interface PerfilRepository {
    suspend fun guardarPerfil(perfil: Perfil): Result<Unit>
    fun obtenerPerfil(): Flow<Perfil?>
}
```

---

## Pasos

1. **Modelos de dominio**: crear `domain/model/Comida.kt` y `domain/model/Perfil.kt`.
2. **Interfaces de repositorio**: `domain/repository/ComidaRepository.kt` y `PerfilRepository.kt`.
3. **Implementaciones Firebase**: `data/repository/ComidaRepositoryFirebaseImpl.kt` y `PerfilRepositoryFirebaseImpl.kt`, mismo patrón que `GlucosaRepositoryFirebaseImpl` (`coleccionUsuario()` con `auth.currentUser?.uid`, `hashMapOf` para escribir, `addSnapshotListener` + `callbackFlow` para leer).
4. **Registrar en Koin** (`di/appModule.kt`):
   ```kotlin
   single<ComidaRepository> { ComidaRepositoryFirebaseImpl(firestore = get(), auth = get()) }
   single<PerfilRepository> { PerfilRepositoryFirebaseImpl(firestore = get(), auth = get()) }
   viewModel { ComidaViewModel(repository = get()) }
   viewModel { PerfilViewModel(repository = get()) }
   ```
5. **ViewModels** (`presentation/viewmodel/`): `ComidaViewModel` y `PerfilViewModel`, exponiendo `StateFlow` y delegando al repositorio — mismo estilo que `GlucosaViewModel`.
6. **Conectar screens**: reemplazar los `remember {}` de `ComidaScreen` y `PerfilInicialScreen` por `koinViewModel()`, igual que ya está hecho en `GlucosaScreen`.
7. **Historial real**: `HistorialViewModel` que reutiliza `GlucosaRepository.obtenerTodasLasMediciones()` (ya existe) para reemplazar lo hardcodeado de `HistorialScreen` (`barras`, card "Promedio", card "Mediciones"):
   - Filtrar los registros de los últimos 7 días.
   - Agrupar por día calendario y promediar `valor` dentro de cada grupo → altura de cada barra del gráfico ("Glucemia diaria", una por día L–D).
   - Promediar **todos** los registros del rango (no el promedio de los promedios diarios) → card "Promedio".
   - Contar el total de registros del rango → card "Mediciones".
   - La sección "Lo que vimos" (observación de texto correlacionando comida/actividad con picos) queda fuera de este alcance — requiere cruzar con `ComidaRepository` y se aborda en una iteración posterior.
8. **Home real**: `HomeViewModel` que usa `GlucosaRepository.obtenerUltimaMedicion()` (ya existe) para reemplazar el valor estático de última glucemia en `HomeScreen`.

### Rotación de pantalla

Ya resuelto para Auth y Glucosa, y se replica igual para Comida/Perfil: el `ViewModel` (instanciado por Koin) vive en el `ViewModelStore` de la Activity y sobrevive a la recreación por rotación. Firestore cubre la persistencia *entre sesiones*; el ViewModel cubre la pérdida de datos *no guardados* durante la sesión.

---

## Orden de implementación sugerido

1. `Comida` + `ComidaRepository` + `ComidaRepositoryFirebaseImpl` + Koin + `ComidaViewModel` → conectar `ComidaScreen`.
2. `Perfil` + `PerfilRepository` + `PerfilRepositoryFirebaseImpl` + Koin + `PerfilViewModel` → conectar `PerfilInicialScreen`.
3. `HistorialViewModel` → conectar `HistorialScreen`.
4. `HomeViewModel` → conectar `HomeScreen`.

---

## Decisiones tomadas

| Decisión | Elección |
|---|---|
| Persistencia | **Firestore** (no Room, no base local) |
| Inyección de dependencias | **Koin** (no Hilt) |
| Entities/DAOs | No aplica — mapeo manual `Map` ↔ modelo de dominio en el repositorio |
| Use cases | No se introducen por ahora — ViewModel llama al repositorio directo, igual que Auth/Glucosa |
| Campos de `Perfil` | **Nombre y diagnóstico** únicamente, guardados en el documento raíz `usuarios/{uid}` |
| `Comida` y actividad física | Un solo modelo (`Comida`), no entidades separadas |
| Gráfico en `HistorialScreen` | Solo glucosa — promedio diario real desde Firestore |
