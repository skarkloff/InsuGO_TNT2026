# Análisis Arquitectónico y Lineamientos de Código — Sprint 1

## Descripción General

**InsuGO** es una aplicación Android móvil diseñada como compañero digital para pacientes con prediabetes/diabetes tipo 2 en el Valle Inferior del Río Chubut. El proyecto se encuentra en etapa de MVP/prototipo al cierre del Sprint 1.

---

## Stack Tecnológico

| Capa | Tecnología | Versión |
|------|-----------|---------|
| Lenguaje | Kotlin | 2.2.10 |
| SDK mínimo | Android 7.0 (Nougat) | API 24 |
| SDK objetivo | Android 15 | API 36 |
| Build system | Gradle (KTS) + AGP | 9.1.1 |
| UI Framework | XML Layouts + ViewBinding | — |
| Testing UI | Espresso | 3.5.1 |
| Testing unitario | JUnit | 4.13.2 |

> Jetpack Compose está incluido en dependencias pero deshabilitado en la compilación actual.

---

## Arquitectura Actual

El proyecto emplea un enfoque **Activity-centric tradicional** sin capas arquitectónicas formales.

### Flujo de navegación

```
MainActivity (Launcher)
    └── LoginActivity
            ├── PerfilInicialActivity → HomeActivity
            └── HomeActivity
                    ├── GlucosaActivity
                    └── AcercaDeActivity
```

### Características del enfoque actual

- Cada pantalla es una `AppCompatActivity` que concentra toda la lógica (UI + negocio)
- **ViewBinding** habilitado — acceso a vistas sin `findViewById`
- Navegación mediante `Intent` manual con flags
- Sin persistencia de datos (todo hardcodeado o en memoria volátil)
- Sin inyección de dependencias

---

## Estructura de Paquetes

```
com.health.insugo/
├── MainActivity.kt            # Launcher → redirige a Login
├── LoginActivity.kt           # Auth hardcodeada (admin / 123456)
├── HomeActivity.kt            # Dashboard principal con datos mock
├── PerfilInicialActivity.kt   # Onboarding del usuario
├── GlucosaActivity.kt         # Registro de glucemia
├── AcercaDeActivity.kt        # Info de la app
└── ui/theme/
    ├── Color.kt               # Paleta de colores Compose
    ├── Theme.kt               # Tema Material Design 3
    └── Type.kt                # Tipografía
```

---

## Convenciones del Proyecto

| Elemento | Convención | Ejemplo |
|----------|-----------|---------|
| Clases | `[Nombre]Activity` | `GlucosaActivity` |
| Bindings | `Activity[Nombre]Binding` | `ActivityLoginBinding` |
| Layouts XML | `activity_[nombre].xml` | `activity_login.xml` |
| Drawables | `bg_[tipo]_[variante].xml` | `bg_btn_primary`, `bg_card_verde` |
| Colores | `[color]_[tono]` | `verde_oscuro`, `gris_600` |
| Constantes | `UPPER_SNAKE_CASE` en `companion object` | `USUARIO`, `PASSWORD` |
| Variables | `camelCase` privadas | `private var glucValue` |

---

## Sistema de Diseño Visual

### Paleta de colores

| Rol | Color | Hex |
|-----|-------|-----|
| Primario | Verde | `#1D9E75` |
| Secundario | Naranja | `#D85A30` |
| Alertas / consejos | Ámbar | `#854F0B` |
| Alertas críticas | Rojo | `#E24B4A` |
| Fondo | Gris cálido | `#F1EFE8` |

Los drawables usan `<shape>` con `<corners radius="12–14dp">` para mantener un estilo redondeado consistente en toda la app.

---

## Patrones de Código Predominantes

### 1. ViewBinding en cada Activity

```kotlin
private lateinit var binding: ActivityLoginBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)
}
```

### 2. Navegación con Intent + flags

```kotlin
startActivity(Intent(this, HomeActivity::class.java).apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
})
```

### 3. Chip de selección única (patrón manual)

```kotlin
val chips = listOf(binding.chipAyunas, binding.chipPostComida, ...)
chips.forEach { chip ->
    chip.setOnClickListener {
        chips.forEach { c -> c.setBackgroundResource(R.drawable.bg_chip_off) }
        chip.setBackgroundResource(R.drawable.bg_chip_on)
    }
}
```

### 4. Teclado numérico (GlucosaActivity)

```kotlin
val keys = mapOf(
    binding.key1 to "1", binding.key2 to "2", ...
)
keys.forEach { (btn, digit) ->
    btn.setOnClickListener { glucKey(digit) }
}
```

---

## Estado de Funcionalidades al Cierre del Sprint 1

| Funcionalidad | Estado |
|--------------|--------|
| Autenticación | Prototipo (credenciales hardcodeadas) |
| Dashboard | Prototipo (datos mock) |
| Registro de glucemia | Funcional |
| Onboarding del usuario | Prototipo |
| Persistencia de datos | No implementado |
| Backend / API REST | No implementado |
| Geolocalización | No implementado |
| API de clima | No implementado |
| Historial y análisis | No implementado |

---

## Lineamientos para Sprints Futuros

A medida que el proyecto escale, se recomienda migrar gradualmente hacia:

1. **MVVM** con `ViewModel` + `LiveData`/`StateFlow` — separar lógica de UI de lógica de negocio
2. **Room** — persistencia local de registros de glucemia y datos de usuario
3. **Navigation Component** — reemplazar la navegación manual con `Intent`
4. **Hilt** — inyección de dependencias para mejor testabilidad y modularidad
5. **Repositorios** — abstraer fuentes de datos (local/remoto) detrás de una interfaz común
6. **Retrofit** — consumir APIs de clima, geolocalización y backend propio
