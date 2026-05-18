# InsuGO

**Taller de Nuevas Tecnologías 2026 — Skarkloff, Zapellini**

InsuGO es una aplicación Android para pacientes con prediabetes o diabetes tipo 2, desarrollada en colaboración con médicos especialistas del Valle Inferior del Río Chubut. Va más allá de un simple anotador: actúa como compañero digital que acompaña al paciente en su cambio de estilo de vida, adaptando sus recomendaciones según sus resultados, su entorno geográfico y el clima de la región.

Los consejos se organizan en categorías como nutrición estacional (priorizando alimentos locales de bajo índice glucémico), actividad física adaptada al clima patagónico, cuidado preventivo de pies, manejo del estrés y alertas sanitarias locales.

El paciente puede registrar glucosa, comidas y actividad física diaria. Al asistir a la consulta, comparte un historial visual semanal con su médico para ajustar el tratamiento con datos reales, sin depender únicamente de los análisis trimestrales.

---

## Arquitectura

Clean Architecture en capas, Single Activity con navegación Compose.

```
com.health.insugo/
├── domain/            Entidades y contratos (sin dependencias de Android)
├── data/              Implementaciones de repositorios (in-memory)
├── presentation/
│   ├── navigation/    NavGraph con las 9 rutas
│   ├── viewmodel/     ViewModels con StateFlow
│   └── ui/            Composables stateless (una Screen por pantalla)
├── ui/theme/          Colores, tipografía y tema
└── MainActivity.kt    Único punto de entrada + grafo de DI manual
```

## Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Navegación | Navigation Compose |
| Estado | ViewModel + StateFlow |
| Build | Gradle con Kotlin DSL |

## Pantallas

| # | Pantalla |
|---|---|
| 1 | Login |
| 2 | Perfil inicial |
| 3 | Inicio / Dashboard |
| 4 | Anotar glucosa |
| 5 | Comida y movimiento |
| 6 | Consejos |
| 7 | Historial semanal |
| 8 | Compartir reporte |
| 9 | Acerca de |

El prototipo navegable completo se encuentra en `docs/prototipo/prototipo_inicial.html`.
