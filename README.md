# Sesión 2 - Android Básico
## Entorno de desarrollo. Filmoteca

Para el desarrollo de esta práctica estoy usando Android Studio desde los siguientes dispositivos:
* Un portátil, potencia limitada
* Torre,
* Mac de clase, conexión a internet limitada
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

Como detalle, destacaré que he marcado el título de la aplicación para que no se pueda traducir.

### Ejercicio 5
Este ejercicio lo encontré más complicado al haber leído solo por encima el apartado del libro que lo explicaba. Al principio me peleé poniendo el código del enunciado sin mucha idea ni criterio además de no saber muy bien qué hacía. Consulté de nuevo con el profesor, cosa que me esclareció bastante las cosas, aunque aún necesitaba una lectura más calmada.

Terminé entendiendo el funcionamiento y la refactorizacón del código. Hasta ahora estábamos usando _Layouts_, así que ese código lo encapsularía en un apartado destinado para ello y el resto sería cosa de _Compose_.

Compose por su parte a pesar de haber leído la documentación y más o menos entenderlo, no paraba de darme errores. Al principio pensaba que era por culpa del código añadido en `AboutActivity.kt`, pero el problema lo tenía con **gradle**.

Una vez solucionado, con seguir un poco la guía del libro y la documentación de jetpack ya lo tenía.

#### Gradle
Mi primer error fue suponer que gradle tendría todo lo necesario puesto y yo no tenía que añadir nada. Después de leer de nuevo el apartado de los apuntes donde se explica un poco más, veo que falta alguna línea de código aunque mi código sigue sin funcionar.

A parte de los apuntes, el propio Android conforme añadía librerías me sugería otras y marcaba errores que podría consultar en la web [Compose Compiler Gradle plugin](https://developer.android.com/develop/ui/compose/compiler).

El resultado fue una mezcla de librerías que tenía el proyecto de por sí, unidas con las que me hacían falta para todos los elementos que usaban _Compose_. Se pueden ver en el archivo `build.gradle.kts` (:app).

Al ver que funcionaban, no podía evitar fijarme en los numerosos _warnings_ que aparecían en algunas. Leyendo un poco vi que algunas librerías hacían lo mismo que otras por tanto tenía duplicidad y no se llegaban  a usar, es por eso que refactoricé un poco.

### Errores
1. Primer día - Error al descargar Device: 
![error](img-readme/error.png)

Puede haber sido por falta de internet o del internet tan lento de la uni. Al cambiarme al Mac de clase con el SSD puedo aprovechar el tiempo que queda para hacer la activitat clase. En casa intentaré descargarlo a ver si es cosa de la mala conexión.

2. Cargado lento del dispositivo de prueba

De los 3 dispositivos que estoy programando, en aquellos con poca potencia se han vuelto tediosas laas pruebas, tanto que parecía que tenia un error en Android Studio cuando no era su culpa.
Un error que ha saltado en el portátil "Se ha evitado una escasedad de memoria..." seguido del cierre de la aplicación. Para nada recomendable.

3. No hacer caso de los _warnings_

He encontrado muy útil el apartado `Problems > Project Errors` que te destaca los errores y warnings del proyecto.
![Problems](img-readme/Problems.jpg)

Al curiosearla y ver warnings no me había preocupado, pero revisando algunos con el profe, se me escapaban algunos como:
- no tener las librerías con la versión recomendada
- dejar cadenas de texto plano en lugar de su identificador `@string` en los elementos de la aplicación.

4. R.string.[algo] error Compose

Usar el identificador para cadenas de texto no funcionaba bien para los textos dentro de _Compose_. Se solucionó con la función `stringResource()`, que ciertamente no es muy intuitiva si en el resto del código lo puedes poner sin la función.

### Conclusiones
En cuanto a los **dispositivos** que he usado puedo clasificar su uso en:
|Dispositivo|Ventajas|Inconvenientes|
|-----------|--------|--------------|
|Portátil|Móbil: uso tanto en clase como en casa|Poca potencia, pantalla pequeña|
|Torre|Buen internet, 2 pantallas, potencia moderada|Solo puedo trabajar en casa|
|Mac|Buena pantalla, a veces va fluido|No estoy acostumbrada a Mac = lentitud, mal internet, a veces no va fluido|

La mejor opción para trabajar es la **torre**, así que intentaré hacer la mayor parte del trabajo en casa e intentaré que las pruebas en clase sean las menos posibles y me centre en dudas.