# MULTITAREA

## Enfoque de concurrencia actual

`MusicPlayerLab` utiliza **Kotlin Coroutines** para ejecutar operaciones de red, parseo y persistencia fuera del hilo principal, manteniendo una UI fluida.

## Corrutinas en el proyecto

Puntos principales de uso:

- `MusicViewModel` lanza tareas con `viewModelScope.launch`.
- `MusicRepository` encapsula operaciones de datos en `withContext(Dispatchers.IO)`.
- `JamendoApiClient` ejecuta operaciones HTTP en `withContext(Dispatchers.IO)`.
- `FavoritesRepository` persiste favoritos en `withContext(Dispatchers.IO)`.

## Uso de `Dispatchers.IO`

`Dispatchers.IO` se aplica para trabajo bloqueante o I/O:

- apertura y lectura de conexiones HTTP
- parseo de respuesta en capa de datos
- lectura/escritura de `SharedPreferences`

Esto evita que dichas operaciones impacten el renderizado Compose.

## Uso de `viewModelScope`

`MusicViewModel` usa `viewModelScope` para:

- iniciar búsquedas remotas
- cancelar la búsqueda previa (`searchJob?.cancel()`) ante una nueva solicitud
- alternar favoritos
- actualizar estado observable de UI y de preview

El ciclo de vida del `ViewModel` controla estas corrutinas y reduce riesgo de fugas.

## Estados de carga y coordinación con UI

El flujo de estado de carga es explícito:

1. antes de la llamada remota: `MusicUiState.Loading`
2. resultado exitoso: `MusicUiState.Success`
3. error: `MusicUiState.Error`

La UI observa estos estados con `collectAsStateWithLifecycle` y renderiza componentes de carga, error, vacío o lista.

## Cómo se evita bloquear la UI

- Red y persistencia se mueven a `Dispatchers.IO`.
- La UI no ejecuta llamadas HTTP ni parseo directamente.
- El `ViewModel` publica estado reactivo (`StateFlow`) para recomposición incremental.
- La cancelación de búsquedas evita trabajo innecesario cuando cambia la consulta.

## Relación con otras capas

- Red y parseo: detallado en [`RED_Y_HTTP.md`](RED_Y_HTTP.md)
- Diseño de capas MVVM: detallado en [`ARQUITECTURA.md`](ARQUITECTURA.md)
- Visión general técnica: [`README_TECNICO.md`](README_TECNICO.md)

## Enlaces relacionados

- Inicio del proyecto: [`../README.md`](../README.md)
- Resumen técnico: [`README_TECNICO.md`](README_TECNICO.md)
- Arquitectura: [`ARQUITECTURA.md`](ARQUITECTURA.md)
- Red y HTTP: [`RED_Y_HTTP.md`](RED_Y_HTTP.md)

