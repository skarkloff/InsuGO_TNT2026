# Plan para persistencia con Room

## Contexto actual

- Sin capa `data/` ni `domain/` — la arquitectura limpia está iniciada solo en `presentation/`
- Las pantallas reciben lambdas directamente desde el NavGraph, sin ViewModels
- No hay inyección de dependencias (sin Hilt)
- Los datos de `GlucosaScreen` y `ComidaScreen` viven únicamente en `remember {}` (se pierden al salir)

---

## Qué vamos a persistir

| Entidad | Campos |
|---|---|
| `GlucosaEntity` | `id`, `valorMgDl`, `momento`, `fechaHora` |
| `ComidaEntity` | `id`, `desayuno`, `almuerzo`, `cena`, `actividad`, `minutosActividad`, `fecha` |
| `PerfilEntity` | `id` (fijo = 1), `nombre`, `diagnostico` |

---

## Pasos

### 1. Dependencias

Agregar en `libs.versions.toml`:
- Versión de Room (ej. `2.6.1`) y de KSP
- Librería `room-runtime`, `room-ktx`, `room-compiler` (para KSP)

Agregar en `build.gradle.kts` del módulo `app`:
- Plugin `com.google.devtools.ksp`
- Las tres dependencias de Room (`implementation` + `ksp` para el compiler)

> Se usa **KSP** (no kapt) porque el proyecto ya está en Kotlin 2.x.

---

### 2. Capa `data/`

Crear el paquete `com.health.insugo.data` con la siguiente estructura:

```
data/
  local/
    db/
      AppDatabase.kt          ← @Database, singleton
    entity/
      GlucosaEntity.kt
      ComidaEntity.kt
      PerfilEntity.kt
    dao/
      GlucosaDao.kt
      ComidaDao.kt
      PerfilDao.kt
  repository/
    GlucosaRepositoryImpl.kt
    ComidaRepositoryImpl.kt
    PerfilRepositoryImpl.kt
```

Cada DAO expone:
- `@Insert` / `@Update` / `@Delete`
- `@Query` que retorna `Flow<List<...>>` para observar cambios reactivamente

---

### 3. Capa `domain/`

Crear el paquete `com.health.insugo.domain`:

```
domain/
  model/
    Glucosa.kt       ← modelo de dominio (distinto de la Entity)
    Comida.kt
    Perfil.kt
  repository/
    GlucosaRepository.kt    ← interface
    ComidaRepository.kt
    PerfilRepository.kt
  usecase/
    glucosa/
      GuardarGlucosaUseCase.kt
      ObtenerGlucosasUseCase.kt
    comida/
      GuardarComidaUseCase.kt
      ObtenerComidasUseCase.kt
    perfil/
      GuardarPerfilUseCase.kt
      ObtenerPerfilUseCase.kt
    historial/
      ObtenerResumenSemanalUseCase.kt   ← calcula promedio diario para el gráfico
```

Los repositorios convierten `Entity ↔ modelo de dominio` (mappers).

Cada use case recibe el repositorio por constructor (`@Inject`) y expone una única función `operator fun invoke(...)`, retornando `Flow` o un resultado suspendido según corresponda.

---

### 4. ViewModels

Crear en `presentation/viewmodel/`:

- `GlucosaViewModel` — expone un `StateFlow<UiState>`, llama al repo para guardar y listar
- `ComidaViewModel` — igual, para los campos de comida y actividad
- `PerfilViewModel` — lee/escribe el perfil del usuario (nombre, diagnóstico)

---

### 5. Hilt — Inyección de dependencias

Agregar en `libs.versions.toml`: versión de Hilt (`2.51.1`) y su plugin KSP.
Agregar en `build.gradle.kts` del proyecto: plugin `com.google.dagger.hilt.android`.
Agregar en `build.gradle.kts` del módulo `app`: plugin Hilt + dependencias `hilt-android` y `hilt-compiler` (ksp).

Crear `InsuGoApplication : Application()` anotada con `@HiltAndroidApp` y registrarla en `AndroidManifest.xml`.

Con Hilt:
- `AppDatabase` se provee como singleton en un `@Module` (`DatabaseModule`)
- Los DAOs se exponen desde ese mismo módulo
- Los repositorios se vinculan interface ↔ impl con `@Binds`
- Los ViewModels usan `@HiltViewModel` + `@Inject constructor(...)`, sin factories manuales
- `InsuGoNavGraph` (o `MainActivity`) se anota con `@AndroidEntryPoint`

---

### 6. Conectar NavGraph con ViewModels

En `InsuGoNavGraph`, obtener cada ViewModel con `viewModel()` de Compose y pasarlo a la pantalla correspondiente, reemplazando los lambdas sueltos actuales.

---

### 7. Historial real

`HistorialScreen` hoy muestra datos hardcodeados (`barras`). Con Room, el `HistorialViewModel` consultará las glucosas de los últimos 7 días desde el DAO y calculará el promedio dinámicamente.

---

## Orden de implementación sugerido

1. Dependencias (KSP + Room + Hilt)
2. Entities + DAOs + AppDatabase
3. `DatabaseModule` de Hilt + `InsuGoApplication` con `@HiltAndroidApp`
4. Repositorios (interface en `domain/` + impl en `data/`) con `@Binds`
5. Use cases en `domain/usecase/` (uno por operación, inyectados con `@Inject`)
6. ViewModel de Glucosa + conectar `GlucosaScreen`
7. ViewModel de Comida + conectar `ComidaScreen`
8. ViewModel de Perfil + conectar `PerfilInicialScreen`
9. ViewModel de Historial + reemplazar datos hardcodeados con datos reales

---

## Decisiones tomadas

| Decisión | Elección |
|---|---|
| Inyección de dependencias | **Hilt** desde el inicio |
| Campos de `PerfilEntity` | **Nombre y diagnóstico** únicamente |
| Gráfico en `HistorialScreen` | **Solo glucosa** — promedio diario real desde Room |
