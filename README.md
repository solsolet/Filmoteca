# Filmoteca
Aplicación de consulta de pelis de un catálogo virtual donde se puede pulsar para ver el detalle de la película, actualizarla, añadir más o eliminarlas. Además se puede visualizar el lugar en el mapa donde fueron grabadas y recibir una alerta cuando el usuario se aproxima al lugar de rodaje.

## Demo
Se puede ver la demo del proyecto en [demo_Filmoteca_Ads.mp4](img-readme/demo_Filmoteca_Ads.mp4). Cualquier problema con la versión entregada por Moodle (tanto del proyecto como del README) se puede usar el repositorio donde se encuentra alojada la práctica: [https://github.com/solsolet/Filmoteca.git](https://github.com/solsolet/Filmoteca.git)

Para probar la aplicación directamente en un dispositivo Android (SDK <= 29) se puede instalar la APK: Filmoteca.apk o descargándola de [GitHub](https://github.com/solsolet/Filmoteca/releases/tag/Ads).

## 📋 Resumen de la arquitectura
```bash
app/src/main/
├── java/es/ua/eps/filmoteca/
│   ├── Filmoteca.kt                  # Clase Aplicación. Contiene el contexto global y el GlobalMode (Bindings/Compose) e inicializa los MobileAds
│   ├── Mode.kt                       # Enum: Bindings | Compose — controla qué sistema de IU está activo
│   ├── UserData.kt                   # Singleton. Almacena la sesión del usuario que ha iniciado (name, email, token)
│   │
│   ├── Film.kt                       # Data class que representa a una película (title, director, year, genre... latitude, longitude, hasGeofence and hasLocation)
│   ├── FilmDataSource.kt             # Lista en memoria de películas compartidas por toda la aplicación
│   │
│   ├── LoginActivity.kt              # Entry point. Controla Google Sign-In via Credential Manager y carga los anuncios intersticiales
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
│   ├── MapsActivity.kt               # Activity que muestra un GoogleMap con la localización de la película
│   ├── GeofenceManager.kt            # # Singleton que registra/elimina geocercas (500 m radio) para películas
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
└── AndroidManifest.xml               # Declara LoginActivity como primera actividad, FCM service, receptor de geocercas y los permisos de notificación
```

## Implementación

### Firebase Messaging
`MyFirebaseMessagingService` se ha adaptado para leer `latitude` y `longitude` del payload de datos y asignarlas a la película recibida.

Las películas creadas/actualizadas desde FCM comienzan con `hasGeofence = false` para que la geocerca solo se active manualmente desde la pantalla de edición.

### Maps
Se ha incorporado el servicio de **Google Maps** para mostrar el lugar de rodaje de una película.

- Se han añadido los campos `latitude`, `longitude` y `hasLocation` al modelo `Film`.
- En `activity_film_data.xml` se añadió un botón `btnShowMap` que solo se hace visible si `film.hasLocation == true`.
- Tanto `FilmDataActivity` y `FilmDataFragment` inician `MapsActivity` pasando el índice de la película.
- `MapsActivity` utiliza un `SupportMapFragment` y `OnMapReadyCallback`.
- Cuando el mapa está listo, se crea un marcador (`MarkerOptions`) en las coordenadas de la película con:
  - **título**: nombre de la película
  - **snippet**: director + año
- La ventana de información del marcador se abre automáticamente con marker.`showInfoWindow()`.
- `activity_maps.xml` es un layout simple que contiene únicamente el `SupportMapFragment` a `match_parent`.
- El API key de Maps se inyecta desde `local.properties` y se declara en el `AndroidManifest.xml` mediante `<meta-data>`.

Esta parte permite al usuario ver la ubicación de rodaje en un mapa interactivo, sin tener que salir de la app.

**Archivos clave:** `MapsActivity.kt`, `activity_maps.xml`, `Film.kt`

### Location
Se ha añadido soporte de localización con **geocercas** para avisar cuando el usuario se acerca al lugar de rodaje.

- `Film.kt` ahora incluye `hasGeofence: Boolean` para saber si la película tiene una geocerca activa.
- En `activity_film_edit.xml` se incluyeron botones para:
  - añadir geocerca
  - eliminar geocerca
- `FilmEditActivity` gestiona la petición de permisos de ubicación **en dos pasos separados**  ya que Android 11+ no permite solicitar `ACCESS_FINE_LOCATION` y `ACCESS_BACKGROUND_LOCATION` en el mismo diálogo:
  1. Primer launcher: solicita `ACCESS_FINE_LOCATION` + `ACCESS_COARSE_LOCATION`.
  2. Segundo launcher: solo después de que el primero sea concedido, solicita `ACCESS_BACKGROUND_LOCATION`.

- `GeofenceManager` (_singleton_) registra geocercas con:
  - Radio: 500 metros
  - Transiciones: de entrada/salida `GEOFENCE_TRANSITION_ENTER` y `GEOFENCE_TRANSITION_DWELL`
  - Disparador inicial: también se activa si el usuario ya está dentro del perímetro cuando se registra
  - Expiración: nunca (`NEVER_EXPIRE`)
- El ID de cada geocerca es el título de la película, lo que permite identificar qué película disparó la transición en el `BroadcastReceiver`.
- `GeofenceBroadcastReceiver` recibe las transiciones de geofencing y muestra una notificación cuando el usuario entra en el perímetro.
- El estado se guarda en el modelo de película para que la UI pueda saber si la geocerca está activa.

Con esto, la app puede alertar al usuario al aproximarse al lugar de grabación de una película.

**Archivos clave:** `GeofenceManager.kt`, `GeofenceBroadcastReceiver.kt`, `FilmEditActivity.kt`

### Ads
Se ha incorporado un anuncio intersticial entre el login y la aplicación.

- `Filmoteca.kt` ahora inicializa `MobileAds`.
- En `LoginActivity.kt` incorpora el anuncio al darle al botón de _login_.
  1. Primero lo inicializamos en una variable a _null_ `interstitialAd` y en `onCreate` llamamos a la función que lo manejará, `loadInterstitialAd`
  2. `loadInterstitialAd` carga el anuncio con el ID correspondiente
  3. Antes del intent que pasa a la página principal de `goToMainActivity`, se llama a `showAdThenNavigate` que muestra el anuncio y controla cualquier posibilidad.

## Problemas encontrados

### Google Sign-In no funciona en el emulador
**Problema:** Al pulsar el botón de Sign In en el emulador (API 36), el selector de cuentas nunca aparecía y el Logcat mostraba `No credentials available`.
 
**Causa:** Credential Manager requiere simultáneamente: un emulador con Google Play Store (no solo Google APIs), una cuenta de Google añadida en Ajustes → Cuentas, y Google Play Services actualizado. En el emulador de API 36 estas condiciones no se cumplían.
 
**Solución:** La funcionalidad se verificó en un dispositivo físico, donde el flujo completo funcionó correctamente. Como anotación general, para que Credential Manager funcione en emulador hay que asegurarse de usar una imagen con Google Play Store, añadir una cuenta de Google, y actualizar Google Play Services desde la Play Store antes de probar, pero a pesar de haber probado estos pasos no se consiguió que funcionase.

---

### AdMob no funciona con cuentas gcloud
**Problema:** No se puede crear una cuenta en AdMob con la cuenta de alumno de *gcloud* que se ha usado para el resto de páginas como Firebase.
 
**Causa:** Las cuentas de _Gcloud_ no tienen el parámetro de fecha de nacimiento del usuario por tanto AdMob no puede verificar la edad del usuario. Se preguntó en una solicitud en _UACloud_ a quien llea los Servicios Externos a ver si me podían ayudar con este tema, pero al ser una cuenta educativa no permite añadir ese campo.
 
**Solución:** Los ID proporcionados en el enunciado son de Google públicos y no hace falta tener una cuenta en AdMob para mostrar esos anuncios, así que al final no ha hecho falta.


## Anotaciones
La aplicación funciona para dispositivos físicos. No se ha logrado que funcionase con el emulador (ver Problema [Google Sign-In](README.md#problemas-encontrados#Google-Sign-In-no-funcionaba-en-el-emulador)).