# Filmoteca
Aplicación de consulta de pelis de un catálogo virtual donde se puede pulsar para ver el detalle de la película, actualizarla, añadir más o eliminarlas.

## Demo
Se puede ver la demo del proyecto en [demo_Filmoteca_CredentialFirebase.mp4](img-readme/demo_Filmoteca_Maps.mp4). Cualquier problema con la versión entregada por Moodle (tanto del proyecto como del README) se puede usar el repositorio donde se encuentra alojada la práctica: [https://github.com/solsolet/Filmoteca.git](https://github.com/solsolet/Filmoteca.git)

Para probar la aplicación directamente en un dispositivo Android (SDK <= 26) se puede instalar la APK: Filmoteca.apk o descargándola de [GitHub](https://github.com/solsolet/Filmoteca/releases/tag/Maps).

## 📋 Resumen de la arquitectura
<!--TODO: añadir las clases que faltan y explicar resumidamente qué hacen-->
```bash
app/src/main/
├── java/es/ua/eps/filmoteca/
│   ├── Filmoteca.kt                  # Clase Aplicación. Contiene el contexto global y el GlobalMode (Bindings/Compose)
│   ├── Mode.kt                       # Enum: Bindings | Compose — controla qué sistema de IU está activo
│   ├── UserData.kt                   # Singleton. Almacena la sesión del usuario que ha iniciado (name, email, token)
│   │
│   ├── Film.kt                       # Data class que representa a una película (title, director, year, genre... latitude, longitude, hasGeofence and hasLocation)
│   ├── FilmDataSource.kt             # Lista en memoria de películas compartidas por toda la aplicación
│   │
│   ├── LoginActivity.kt              # Entry point. Controla Google Sign-In via Credential Manager
│   ├── MainActivity.kt               # Muestra la actividad por fragmentos (móvil: 1, tablet: 2)
│   ├── FilmListFragment.kt           # Fragmento que muestra la lista de películas. Contiene el toolbar menu (add, about, sign out)
│   ├── FilmDataFragment.kt           # Fragmento que muestra el detalle de la película seleccionada
│   │
│   ├── FilmDataActivity.kt           # Standalone activity para mostrar la película seleccionada (fuera de flow del fragmento)
│   ├── FilmEditActivity.kt           # Actividad para editar los datos de la película (ahora con botones para añadir/eliminar geocerca y pide permisos de ubicación)
│   ├── AboutActivity.kt              # "About" screen — también muestra el nombre del usuario que ha iniciado la sesión
│   │
│   ├── FilmsArrayAdapter.kt          # ArrayAdapter para la ListView en FilmListActivity
│   ├── FilmsFragmentAdapter.kt       # Variante de ArrayAdapter usado en FilmListFragment
│   ├── FilmsAdapter.kt               # RecyclerView adapter
│   │
│   ├── MapsActivity.kt               # Activity que muestra un GoogleMap con la localización de la película seleccionada
│   ├── GeofenceManager.kt            # Clase que registra / elimina geocercas (500 m) para películas con ubicación
│   ├── GeofenceBroadcastReceiver.kt  # Receptor de transiciones de geocerca que genera notificaciones
│   │
│   └── MyFirebaseMessagingService.kt # Servicio FCM. Recibe data messages y añade/actualiza/elimina películas. Actualizado para parsear latitude/longitude
│
├── res/
│   ├── layout/
│   │   ├── activity_main.xml         # FrameLayout + MaterialToolbar — contenedor para los fragmentos en el móvil
│   │   ├── activity_login.xml        # Login screen con botón Google Sign-In
│   │   ├── activity_film_list.xml    # Standalone film list (usado en FilmListActivity)
│   │   ├── activity_film_data.xml    # Detalle de la película. Ahora con botón "Show on map" que se muestra si la película tiene ubicación
│   │   ├── activity_film_edit.xml    # Formulario para editar la película
│   │   ├── activity_about.xml        # About screen
│   │   │
│   │   ├── activity_maps.xml         # SupportMapFragment a pantalla completa
│   │   └── item_peli.xml             # Fila única de película para la lista de pelis
│   │
│   ├── layout-large/                 # Tablet layouts
│   │   └── activity_main.xml         # MaterialToolbar + horizontal LinearLayout con los dos fragmentos, uno a a cada lado
│   │
│   ├── layout-land/                  # Landscape layouts
│   │   └── activity_film_data.xml
│   │
│   └── menu/
│       ├── film_list_menu.xml        # Toolbar menu: New Film, About, Close Session, Disconnect Account
│       └── film_list_contextual_menu.xml  # Contextual action menu para eliminar pelis con selección múltiple
│
└── AndroidManifest.xml               # Declara LoginActivity como primera actividad, FCM service y los permisos de notificación
```

## Implementación
### Credential Manager
La autenticación se implementa usando la API moderna **Credential Manager** de AndroidX, que reemplaza el antiguo `GoogleSignIn` de Play Services (ahora obsoleto).
 
**Flujo de autenticación:**
 
1. `LoginActivity` es el punto de entrada de la app (declarada como `LAUNCHER` en el Manifest).
2. Al pulsar el botón de *Sign In*, se construye un `GetGoogleIdOption` con el **Web Client ID** registrado en Google Cloud Console y se lanza `CredentialManager.getCredential()` dentro de una coroutine (`lifecycleScope.launch`).
3. El sistema muestra el selector nativo de cuentas de Google.
4. Al seleccionar una cuenta, se recibe un `GoogleIdTokenCredential` del que se extrae el nombre, email, foto e ID token del usuario.
5. Estos datos se almacenan en el singleton `UserData`, que actúa como fuente de verdad de la sesión durante toda la ejecución de la app.
6. Se redirige a `MainActivity` con `FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK` para eliminar `LoginActivity` del back stack — el usuario no puede volver a la pantalla de login con el botón Atrás.
**Cierre de sesión:**
 
Desde el menú de `FilmListFragment` hay dos opciones:
- **Close session** (`signOut`): limpia `UserData` y vuelve a `LoginActivity`.
- **Disconnect account** (`disconnect`): además llama a `CredentialManager.clearCredentialState()`, que revoca el acceso almacenado en el sistema para que la próxima vez se muestre el selector de cuenta en lugar de autenticar silenciosamente.
**Archivos clave:** `LoginActivity.kt`, `UserData.kt`, `FilmListFragment.kt`, `activity_login.xml`, `AndroidManifest.xml`

### Firebase Messaging
Se usa **FCM** para recibir mensajes de datos (*data messages*) que añaden, actualizan o eliminan películas del catálogo en tiempo real.
 
**Tipo de mensaje utilizado — Data Message:**
 
A diferencia de los *notification messages* (que el sistema muestra automáticamente), los *data messages* siempre despiertan el servicio `onMessageReceived`, tanto si la app está en primer plano como en segundo. Esto es necesario para poder modificar `FilmDataSource` desde el servicio.
 
**Formato del payload esperado:**
 
| Clave       | Valores posibles        | Descripción                        |
|-------------|-------------------------|------------------------------------|
| `operation` | `"add"` / `"delete"`    | Tipo de operación                  |
| `title`     | Texto                   | Título de la película (obligatorio)|
| `director`  | Texto                   | Director (opcional en delete)      |
| `year`      | Número como String      | Año (opcional en delete)           |
| `comments`  | Texto                   | Comentarios (opcional en delete)   |
 
**Lógica de negocio en `MyFirebaseMessagingService`:**
 
- `operation = "add"`: busca una película con el mismo título en `FilmDataSource.films`.
  - Si existe → la **actualiza** en su posición (misma posición en la lista).
  - Si no existe → la **añade** al final.
- `operation = "delete"`: busca por título y la elimina. Si no existe, no hace nada.
- En ambos casos muestra una notificación del sistema con el título de la operación y el nombre de la película.
**Archivos clave:** `MyFirebaseMessagingService.kt`, `AndroidManifest.xml`, `google-services.json` (no versionado)

- `MyFirebaseMessagingService` se ha adaptado para leer `latitude` y `longitude` del payload de datos y asignarlas a la película recibida.
- Las películas creadas/actualizadas desde FCM comienzan con `hasGeofence = false` para que la geocerca solo se active manualmente desde la pantalla de edición.

### Maps
<!--TODO: explicar qué hace Maps activity, funciones más importantes brevemente, parovechar los conocimientos de los comentarios-->
Se ha incorporado el servicio de Google Maps para mostrar el lugar de rodaje de una película.

- Se han añadido los campos `latitude`, `longitude` y `hasLocation` al `data class Film`.
- En `activity_film_data.xml` se añadió un botón `btnShowMap` que solo se hace visible si `film.hasLocation == true`.
- `FilmDataActivity` y `FilmDataFragment` inician `MapsActivity` pasando el índice de la película.
- `MapsActivity` utiliza un `SupportMapFragment` y `OnMapReadyCallback`.
- Cuando el mapa está listo, se crea un marcador (`MarkerOptions`) en las coordenadas de la película. El marcador muestra:
  - título: nombre de la película
  - snippet: director + año
- `activity_maps.xml` es un layout simple que contiene únicamente el `SupportMapFragment` a `match_parent`.
- El API key de Maps se inyecta desde `local.properties` y se declara en el `AndroidManifest.xml` mediante `<meta-data>`.

Esta parte permite al usuario ver la ubicación de rodaje en un mapa interactivo, sin tener que salir de la app.

### Location
<!--TODO: explicar qué hace GeofenceMAnager y GeofenceBroadcasttReceiver, funciones más importantes brevemente, parovechar los conocimientos de los comentarios-->
### Location
Se ha añadido soporte de localización con geocercas para avisar cuando el usuario se acerca al lugar de rodaje.

- `Film.kt` ahora incluye `hasGeofence` para saber si la película tiene una geocerca activa.
- En `activity_film_edit.xml` se incluyeron botones para:
  - añadir geocerca
  - eliminar geocerca
- `FilmEditActivity` gestiona la petición de permisos de ubicación y llama a `GeofenceManager` para crear o borrar la geocerca.
- `GeofenceManager` registra geocercas con radio de 500 metros y transiciones de entrada/salida según la práctica.
- `GeofenceBroadcastReceiver` recibe las transiciones de geofencing y muestra una notificación cuando el usuario entra en el perímetro.
- El estado se guarda en el modelo de película para que la UI pueda saber si la geocerca está activa.

Con esto, la app puede alertar al usuario al aproximarse al lugar de grabación de una película.

## Problemas encontrados
### 1. Contenido bajo la cámara/notch (edge-to-edge)
**Problema:** En dispositivos con notch, la lista de películas aparecía por debajo de la cámara sin respetar el padding superior.
 
**Causa:** A partir de Android 15 (API 35), el modo edge-to-edge es obligatorio aunque no se llame a `enableEdgeToEdge()`. El contenido empieza en y=0, debajo de la barra de estado.
 
**Solución:** Se añadió un `ViewCompat.setOnApplyWindowInsetsListener` sobre la `MaterialToolbar` de `MainActivity`, aplicando `bars.top` como padding superior. De esta forma la toolbar se desplaza automáticamente para dejar espacio a la barra de estado y el fragment container queda naturalmente por debajo.
 
---
 
### 2. El menú del fragment no aparecía en MainActivity
**Problema:** Las opciones del menú de `FilmListFragment` (Añadir, Cerrar sesión, etc.) no se mostraban porque `MainActivity` no tenía `ActionBar`.
 
**Causa:** `FilmListFragment` llama a `setHasOptionsMenu(true)` e infla `film_list_menu.xml`, pero esto requiere que la Activity anfitriona tenga un `ActionBar/Toolbar` registrado mediante `setSupportActionBar()`. Las demás activities sí lo tenían; `MainActivity` no.
 
**Solución:** Se añadió una `MaterialToolbar` a los layouts `activity_main.xml` (teléfono) y `layout-large/activity_main.xml` (tablet), y se llamó a `setSupportActionBar(findViewById(R.id.mtMainMenu))` en `MainActivity.onCreate()`.
 
---
 
### 3. Conflicto de versiones de Firebase Messaging en Gradle
**Problema:** Al añadir la dependencia de FCM, el build fallaba con `Could not find com.google.firebase:firebase-messaging-ktx:34.12.0`.
 
**Causa:** En `libs.versions.toml` se declaró `firebase-messaging` con `version.ref = "firebaseBom"`, lo que le asignaba el número de versión del BOM (`34.12.0`) como versión propia de la librería — cuando en realidad el BOM es un *catálogo de versiones*, no una versión directamente aplicable a `firebase-messaging-ktx`. Además había una segunda entrada duplicada para la misma librería.
 
**Solución:** Se eliminó la versión individual de `firebase-messaging` en TOML y se declaró sin `version.ref`, dejando que el BOM (`platform(libs.firebase.bom)`) resuelva la versión correcta automáticamente. También se eliminó la entrada duplicada `firebase-messaging-ktx`.
 
---
 
### 4. Notificaciones no aparecían en Android 13+
**Problema:** El servicio FCM procesaba los mensajes correctamente (los films se añadían/eliminaban) pero no se mostraba ninguna notificación en el dispositivo.
 
**Causa:** Desde Android 13 (API 33), las apps deben solicitar el permiso `POST_NOTIFICATIONS` en tiempo de ejecución. Sin este permiso, `notificationManager.notify()` se ejecuta silenciosamente sin mostrar nada. Con `targetSdk = 36`, este permiso no se otorga automáticamente.
 
**Solución:** Se declaró `<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>` en el Manifest y se implementó la solicitud en tiempo de ejecución en `MainActivity` usando `registerForActivityResult(ActivityResultContracts.RequestPermission())`, que debe registrarse como propiedad de la clase (no dentro de una función) por restricciones del lifecycle de Android.
 
---
 
### 5. Google Sign-In no funcionaba en el emulador
**Problema:** Al pulsar el botón de Sign In en el emulador (API 36), el selector de cuentas nunca aparecía y el Logcat mostraba `No credentials available`.
 
**Causa:** Credential Manager requiere simultáneamente: un emulador con Google Play Store (no solo Google APIs), una cuenta de Google añadida en Ajustes → Cuentas, y Google Play Services actualizado. En el emulador de API 36 estas condiciones no se cumplían.
 
**Solución:** La funcionalidad se verificó en un dispositivo físico, donde el flujo completo funcionó correctamente. Como anotación general, para que Credential Manager funcione en emulador hay que asegurarse de usar una imagen con Google Play Store, añadir una cuenta de Google, y actualizar Google Play Services desde la Play Store antes de probar, pero a pesar de haber probado estos pasos no se consiguió que funcionase.

### API key en gradle
<!--TOOD: explciar problema que no sabía como poner la api key de manera que no se subiese al repo remoto por tanto pensé en ponerla en el arichivo de  local.properties pero me costó que build.gradle (module app) lo detectase bien-->

### API key not found
<!--TODO: explcia que no encontraba la API key en el manifest por que la etiqueta meta-data no estaba correctamente ubicada dentro de application-->

## Anotaciones
La aplicación funciona para dispositivos físicos. No se ha logrado que funcionase con el emulador (ver Problema 5).
Las pausas que se ven en la demo de la aplicación es por que se está lanzando una nueva campaña para mostrar la notificación.