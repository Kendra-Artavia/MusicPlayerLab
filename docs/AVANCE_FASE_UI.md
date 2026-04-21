# AVANCE_FASE_UI

## Propósito del documento

Este documento consolida el avance de la fase de interfaz en `MusicPlayerLab` y mantiene coherencia con la documentación técnica actual en `docs/`.

## Estado actual de la fase de UI

- UI implementada con Jetpack Compose y Material 3.
- Pantalla principal operativa con `MainActivity` + `MusicPlayerLabApp` + `HomeScreen`.
- Integración directa con `MusicViewModel` mediante `StateFlow`.
- Carga inicial y búsqueda de canciones disponibles desde la pantalla principal.

## Alcance funcional implementado

### Estados de UI

Estados visuales implementados en Compose:

- `Idle`
- `Loading`
- `Success`
- `Error`

Además, en `Success` se contempla estado vacío con mensajes contextuales.

### Búsqueda y filtros

- campo de búsqueda con acción de teclado y botón de búsqueda,
- limpieza de consulta,
- filtros:
  - `ALL`
  - `FAVORITES_ONLY`.

### Lista de canciones

- renderizado con `LazyColumn`,
- ítems con título, artista y metadatos opcionales,
- acción de recarga disponible.

### Favoritos

- marcado/desmarcado por canción,
- indicador visual de favorito,
- filtrado por favoritos,
- persistencia en `SharedPreferences` a través de `FavoritesRepository`.

### Preview de audio

- acciones `Play/Pause` y `Stop`,
- indicadores de preparación y reproducción,
- coordinación desde `PreviewPlaybackState`.

## Integración técnica validada

Archivos principales de UI:

- `app/src/main/java/com/example/musicplayerlab/MainActivity.kt`
- `app/src/main/java/com/example/musicplayerlab/ui/screens/HomeScreen.kt`
- `app/src/main/java/com/example/musicplayerlab/ui/components/HomeSearchBar.kt`
- `app/src/main/java/com/example/musicplayerlab/ui/components/MusicStateViews.kt`

Acoplamiento con ViewModel:

- observación con `collectAsStateWithLifecycle`,
- eventos de UI delegados a métodos del `MusicViewModel`,
- UI sin llamadas directas a red ni parseo.

## Recursos y localización

- textos visibles centralizados en `app/src/main/res/values/strings.xml`,
- pluralización en `app/src/main/res/values/plurals.xml`.

## Alcance y límites

La fase actual se mantiene en una sola pantalla principal y sin navegación entre pantallas. El objetivo sigue siendo consolidar flujo de estados, búsqueda, favoritos y preview sobre arquitectura MVVM existente.

## Relación con la fase de datos

La UI consume estados derivados de la capa de datos a través de `MusicViewModel`, conservando separación de responsabilidades.

## Enlaces relacionados

- Inicio del proyecto: [`../README.md`](../README.md)
- Resumen técnico: [`README_TECNICO.md`](README_TECNICO.md)
- Arquitectura: [`ARQUITECTURA.md`](ARQUITECTURA.md)
- Red y HTTP: [`RED_Y_HTTP.md`](RED_Y_HTTP.md)
- Procesamiento JSON: [`PROCESAMIENTO_JSON.md`](PROCESAMIENTO_JSON.md)
- Componente del dispositivo: [`COMPONENTE_DISPOSITIVO.md`](COMPONENTE_DISPOSITIVO.md)
- Multitarea: [`MULTITAREA.md`](MULTITAREA.md)
- UI Compose: [`UI_COMPOSE.md`](UI_COMPOSE.md)
- Ejecución y pruebas: [`EJECUCION_Y_PRUEBAS.md`](EJECUCION_Y_PRUEBAS.md)
- Avance fase datos: [`AVANCE_FASE_DATOS.md`](AVANCE_FASE_DATOS.md)

