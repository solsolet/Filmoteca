# Sesión 2
## Filmoteca
### Ejercicio 1
Primero he cambiado todo lo que se llama "main" y lo he sustituido por "about", p. ej. "`MainActivity.kt` -> `AboutActivity.kt`". Dentro de este archivo quité también el "ViewCompat". 
siguiedo con estos cambios tambien en el AndroidManifest he tenido cuidado que el archivo aparezca correctamente.

Por último, con el recurso about_activity.xml más de lo mismo.

### Ejercicio 2
Probarlo en el Mac ha sido sencillo, ha salido lo esperado.

Previamente lo estaba probando con mi portàtil y no me salía nada con el mismo código. Probé lo de intalar HAXM y no funcionó. Intenté también la conexión de mi móbil por USB con el portátil y aunque la conexión funcionó, la ejecución de la aplicación no.

Sería cosa del portàtil en sí, puede que tenga o poca memòria para ejecutar la aplicación, me falte por descargar alguna cosa...

En casa que tengo una torre con un poco más de potencia volví a probar y si que fue. Como aclaración el portátil y la torre tienen Linux, la distribución de Manjaro.
* Mi portátil tiene 8 GB de RAM.
* La torre tiene 16 GB de RAM.

### Ejercicio 3
Le he puesto los elementos comentados en el ejercicio y los he centrado con "constraitHorizontal" y los he espaciado. Los botones en `AboutActivity` los he nombrado como variables y luego les he añadido un listener que al clocar muestre el Toast de "_Funcionalidad sin implementar_".

Al probar la aplicación ha funcionado como esperaba.

### Ejercicio 4
He seguido los pasos para la internacionalización. Al crear la nueva carpeta, `res/values-en`, veo que no se me crea nada en Android Studio. Al preguntar el profe me comenta que es cosa de Mac: si creas una carpeta por defecto no aparece aunque está.
La encuentro en el explorador y le añado un fichero para poder verla, en este caso una copia de `strings.xml`.

Uso la opción de Android Studio para traducir más cómodamente las _strings_, el editor de traducciones:
![Editor de Traducciones](img-readme/Translations-Editor.png) 

### Ejercicio 5
Este ejercicio lo encuentro más complicado al haber leído solo por encima el apartado del libro que lo explicaba. Al principio me peleo poniendo el código del enunciado sin mucha idea y criterio además de no saber muy bien qué hace. Consulto de nuevo con el profesor cosa que esclarece bastante las cosas aunque aún necesitaba una lectura más calmada.

Termino entendiendo el funcionamiento y la refactorizacón del código. Hasta ahora estábamos usando _Layouts_ así que ese código lo encapsularía en un apartado destinado para ello y el resto sería cosa de _Compose_.

Compose por su parte a pesar de haber leído la documentación y más o menos entenderlo no paraba de darme errores. Al principio penaba que era por culpa del código añadido en `AboutActivity.kt`, pero el problema lo tenía con gradle.

#### Gradle
Mi primer error fue suponer que gradle lo tendría todo y yo no tenía que añadir nada. Despúes de leer de nuevo el apartado de los apuntes donde se explica un poco más veo que falta alguna línea de código aunque mi código sigue sin funcionar.

### Errores
Error al descargar Device: 
![alt text](img-readme/image.png)
Pot haver sigut per falta d'internet o de l'internet tan lent de la uni. Em canvie al iMac de classe amb el SSD per a fer l'activitat en el temps que ens queda. En casa intentaré descarregar a vore si em deixa el mòbil.

tal

Se ha evitado una escasedad de memoria
