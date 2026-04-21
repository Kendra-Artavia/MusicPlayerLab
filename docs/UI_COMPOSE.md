# UI_COMPOSE

## Estado real de la interfaz actual

La interfaz de `MusicPlayerLab` está implementada con **Jetpack Compose** y se concentra en una pantalla principal que integra búsqueda, listado de canciones, manejo de estados de carga, favoritos y control de preview.

## Base tecnológica de UI

- Jetpack Compose
- Material 3
- `collectAsStateWithLifecycle` para observar estado de `StateFlow`

Archivos clave:

- `app/src/main/java/com/example/musicplayerlab/MainActivity.kt`
- `app/src/main/java/com/example/musicplayerlab/ui/screens/HomeScreen.kt`
- `app/src/main/java/com/example/musicplayerlab/ui/components/HomeSearchBar.kt`
- `app/src/main/java/com/example/musicplayerlab/ui/components/MusicStateViews.kt`

## Pantalla principal

`MainActivity` monta `MusicPlayerLabApp()` dentro del tema `MusicPlayerLabTheme`.

`MusicPlayerLabApp`:

- dispara carga inicial con `LaunchedEffect` (`loadInitialSongs()`),
- observa estado desde `MusicViewModel`,
- delega renderizado y acciones a `HomeScreen`.

## Estados visuales implementados

El estado visual depende de `MusicUiState`:

- `Idle`
- `Loading`
- `Success`
- `Error`

Renderizado por estado:

- `CenteredMessageState` para `Idle`
- `LoadingState` para carga
- `SongListSuccessState` para lista exitosa
- `EmptyState` para resultados vacíos
- `ErrorState` para errores con acción de reintento

## Lista de canciones

La lista se renderiza con `LazyColumn` en `SongListSuccessState`.

Cada ítem (`SongRow`) muestra:

- título
- artista
- álbum (si existe)
- duración (si existe)
- indicador visual de portada (placeholder cuando hay URL de imagen)

## Búsqueda

`HomeSearchBar` implementa:

- campo de texto con acción de teclado `Search`,
- botón de buscar,
- botón de limpiar cuando hay consulta,
- filtros por lista:
  - `SongListFilter.ALL`
  - `SongListFilter.FAVORITES_ONLY`

La búsqueda es gestionada por `MusicViewModel` (`onSearch`, `onSearchQueryChange`) y mantiene coherencia con el contenido inicial/cache.

## Reproducción de preview

La reproducción de preview está implementada en el estado actual.

Desde UI:

- acción `Play/Pause` por canción,
- acción `Stop` para preview activo,
- indicadores `Preparando` y `Reproduciendo`.

Desde lógica:

- `MusicViewModel` coordina `PreviewPlaybackState`.
- `PreviewPlayerManager` usa `MediaPlayer` para play/pause/resume/stop.

## Favoritos

La funcionalidad de favoritos está implementada.

Desde UI:

- `FilterChip` por canción para marcar/desmarcar favorito,
- indicador visual de canción favorita,
- filtro de listado solo favoritos.

Persistencia:

- `FavoritesRepository` guarda IDs en `SharedPreferences`.
- `MusicViewModel` expone `favoriteSongIds` y combina este flujo con canciones/filtro.

## Separación entre UI y lógica

La UI permanece declarativa y sin acceso directo a red o parseo. Toda lógica de negocio y datos se gestiona en `MusicViewModel` y capas inferiores.

## Enlaces relacionados

- Inicio del proyecto: [`../README.md`](../README.md)
- Resumen técnico: [`README_TECNICO.md`](README_TECNICO.md)
- Arquitectura: [`ARQUITECTURA.md`](ARQUITECTURA.md)
- Red y HTTP: [`RED_Y_HTTP.md`](RED_Y_HTTP.md)
- Procesamiento JSON: [`PROCESAMIENTO_JSON.md`](PROCESAMIENTO_JSON.md)
- Componente del dispositivo: [`COMPONENTE_DISPOSITIVO.md`](COMPONENTE_DISPOSITIVO.md)
- Multitarea: [`MULTITAREA.md`](MULTITAREA.md)
- Ejecución y pruebas: [`EJECUCION_Y_PRUEBAS.md`](EJECUCION_Y_PRUEBAS.md)
- Avance fase datos: [`AVANCE_FASE_DATOS.md`](AVANCE_FASE_DATOS.md)
- Avance fase UI: [`AVANCE_FASE_UI.md`](AVANCE_FASE_UI.md)

