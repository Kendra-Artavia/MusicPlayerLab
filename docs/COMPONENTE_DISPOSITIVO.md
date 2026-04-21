# COMPONENTE_DISPOSITIVO

## Componente integrado en el estado actual

El componente del dispositivo integrado de forma explícita en esta fase es la **conectividad de red** (estado de internet disponible/no disponible), utilizada como precondición para el consumo HTTP.

## Permisos usados

Declarados en `app/src/main/AndroidManifest.xml`:

- `android.permission.INTERNET`
- `android.permission.ACCESS_NETWORK_STATE`

## Utilidad creada para detectar conexión

Se implementa una abstracción y su implementación Android:

- `NetworkConnectivityChecker` (interfaz)
- `AndroidNetworkConnectivityChecker` (implementación)

Archivo:

- `app/src/main/java/com/example/musicplayerlab/utils/NetworkConnectivityChecker.kt`

Criterio actual de conexión válida:

- `NET_CAPABILITY_INTERNET`
- `NET_CAPABILITY_VALIDATED`

La implementación usa `ConnectivityManager` y `NetworkCapabilities` para evaluar conectividad activa.

## Influencia en el repository

`MusicRepository` integra la verificación antes de invocar `JamendoApiClient`:

1. Llama `connectivityChecker.isInternetAvailable()`.
2. Si no hay conectividad, retorna `NetworkResult.Error(type = ErrorType.NO_INTERNET)`.
3. Si hay conectividad, continúa con consumo HTTP y parseo JSON.

Archivo principal:

- `app/src/main/java/com/example/musicplayerlab/repository/MusicRepository.kt`

Este comportamiento evita intentos de red innecesarios y permite una respuesta de error controlada hacia la capa de presentación.

## Relación con la UI

`MusicViewModel` mapea `ErrorType.NO_INTERNET` a recurso de texto (`R.string.error_no_internet`) y la UI muestra el estado de error correspondiente.

Archivos relacionados:

- `app/src/main/java/com/example/musicplayerlab/viewmodel/MusicViewModel.kt`
- `app/src/main/java/com/example/musicplayerlab/ui/screens/HomeScreen.kt`

## Justificación frente al requisito de laboratorio

Este componente cumple el requisito de integración con recursos/capacidades del dispositivo porque:

- usa servicios del sistema Android para inspeccionar estado de red,
- condiciona el comportamiento funcional de la app en tiempo de ejecución,
- participa en la robustez de la capa de datos y en la experiencia de usuario al evitar fallos de red no gestionados.

## Alcance y límites documentados

En el estado actual, la documentación de componente de dispositivo se centra en conectividad de red, que es la integración explícita exigida y operativa en el flujo de datos.

## Enlaces relacionados

- Inicio del proyecto: [`../README.md`](../README.md)
- Resumen técnico: [`README_TECNICO.md`](README_TECNICO.md)
- Arquitectura: [`ARQUITECTURA.md`](ARQUITECTURA.md)
- Red y HTTP: [`RED_Y_HTTP.md`](RED_Y_HTTP.md)
- Procesamiento JSON: [`PROCESAMIENTO_JSON.md`](PROCESAMIENTO_JSON.md)
- Multitarea: [`MULTITAREA.md`](MULTITAREA.md)
- UI Compose: [`UI_COMPOSE.md`](UI_COMPOSE.md)
- Ejecución y pruebas: [`EJECUCION_Y_PRUEBAS.md`](EJECUCION_Y_PRUEBAS.md)
- Avance fase datos: [`AVANCE_FASE_DATOS.md`](AVANCE_FASE_DATOS.md)
- Avance fase UI: [`AVANCE_FASE_UI.md`](AVANCE_FASE_UI.md)

