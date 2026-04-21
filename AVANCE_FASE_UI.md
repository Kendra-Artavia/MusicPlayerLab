# Avance de la fase de interfaz grafica - MusicPlayerLab

> Documento de seguimiento de UI separado de `AVANCE_FASE_DATOS.md`.

## Estado actual (fase 1)
- Compose configurado en el modulo `app`.
- Pantalla principal activa con `MainActivity` + `setContent`.
- Integracion con `MusicViewModel` existente (sin cambiar arquitectura MVVM).
- Carga inicial de canciones al entrar a la pantalla.

## Alcance implementado en esta fase
- Estados visuales base en Compose:
  - `Idle`
  - `Loading`
  - `Success`
  - `Error`
- Lista de canciones en `Success` con `LazyColumn`.
- Item de cancion con estructura simple en Material 3:
  - titulo
  - artista
  - metadatos opcionales (album, duracion, indicador de portada)
- Acciones de usuario minimas:
  - reintento en error
  - recarga manual en estado exitoso/vacio

## Integracion tecnica validada
- Conexion Activity -> Compose -> ViewModel:
  - `app/src/main/java/com/example/musicplayerlab/MainActivity.kt`
  - `app/src/main/java/com/example/musicplayerlab/ui/screens/HomeScreen.kt`
- Observacion de estado con `collectAsStateWithLifecycle`.
- Carga inicial con `LaunchedEffect` reutilizando `MusicViewModel.loadInitialSongs()`.
- Componentes reutilizables por estado en:
  - `app/src/main/java/com/example/musicplayerlab/ui/components/MusicStateViews.kt`

## Recursos y textos
- No hay strings visibles hardcodeados en composables.
- Textos visibles centralizados en recursos:
  - `app/src/main/res/values/strings.xml`
- Conteo de canciones en plural correctamente localizado:
  - `app/src/main/res/values/plurals.xml`

## Restricciones mantenidas
- Sin navegacion.
- Sin reproductor.
- Sin logica de negocio en componentes de UI.
- Sin cambios de arquitectura fuera de lo necesario para conectar pantalla y estados.

## Pendiente menor de siguiente iteracion UI
- Agregar pruebas de UI Compose para validar rendering por estado:
  - `Loading`
  - `Error`
  - `Success vacio`
  - `Success con lista`

