# Filmoteca
Aplicación de consulta de pelis de un catálogo virtual donde se puede pulsar para ver el detalle de la película, actualizarla, añadir más o eliminarlas. Además se puede visualizar el lugar en el mapa donde fueron grabadas.

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

### Firebase Messaging
`MyFirebaseMessagingService` se ha adaptado para leer `latitude` y `longitude` del payload de datos y asignarlas a la película recibida.

Las películas creadas/actualizadas desde FCM comienzan con `hasGeofence = false` para que la geocerca solo se active manualmente desde la pantalla de edición.

### Maps
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
### Contenido bajo la cámara/notch (edge-to-edge)
**Problema:** En dispositivos con notch, la lista de películas aparecía por debajo de la cámara sin respetar el padding superior.
 
**Causa:** A partir de Android 15 (API 35), el modo edge-to-edge es obligatorio aunque no se llame a `enableEdgeToEdge()`. El contenido empieza en y=0, debajo de la barra de estado.
 
**Solución:** Se añadió un `ViewCompat.setOnApplyWindowInsetsListener` sobre la `MaterialToolbar` de `MainActivity`, aplicando `bars.top` como padding superior. De esta forma la toolbar se desplaza automáticamente para dejar espacio a la barra de estado y el fragment container queda naturalmente por debajo.
 
---
 
### Google Sign-In no funcionaba en el emulador
**Problema:** Al pulsar el botón de Sign In en el emulador (API 36), el selector de cuentas nunca aparecía y el Logcat mostraba `No credentials available`.
 
**Causa:** Credential Manager requiere simultáneamente: un emulador con Google Play Store (no solo Google APIs), una cuenta de Google añadida en Ajustes → Cuentas, y Google Play Services actualizado. En el emulador de API 36 estas condiciones no se cumplían.
 
**Solución:** La funcionalidad se verificó en un dispositivo físico, donde el flujo completo funcionó correctamente. Como anotación general, para que Credential Manager funcione en emulador hay que asegurarse de usar una imagen con Google Play Store, añadir una cuenta de Google, y actualizar Google Play Services desde la Play Store antes de probar, pero a pesar de haber probado estos pasos no se consiguió que funcionase.

---

### API key en gradle
**Problema:** Al _buidear_ la aplicación no me fijé y no puse el valor de la API key en el `AndroidManifest`, dejando el valor por defecto. Como la API key es un dato sensible no sabía donde ponerlo.
 
**Causa:** Para que <meta-data> funcione requiere de un valor válido.
 
**Solución:** Poner una variable en `local.properties` con el valor de la API key y en `build.gradle (:app)` poner en la configuración que cuando vea `"MAPS_API_KEY"` sustituya la variable por el valor correspondiente.

### API key not found
<!--TODO: explcia que no encontraba la API key en el manifest por que la etiqueta meta-data no estaba correctamente ubicada dentro de application-->
**Problema:** No se encuentra la API key a la hora de hacer la _build_.
 
**Causa:** Para que <meta-data> funcione debe estar bien colocada en el `AndroidMAnifest`.
 
**Solución:** Mover la línea de <meta-data> dentro de la etiqueta <application>.

## Anotaciones
La aplicación funciona para dispositivos físicos. No se ha logrado que funcionase con el emulador (ver Problema [Google Sign-In](README.md#problemas-encontrados#Google-Sign-In-no-funcionaba-en-el-emulador)).