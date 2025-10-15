# Sesión 2 - Android Básico
## Entorno de desarrollo. Filmoteca

Para el desarrollo de esta práctica estoy utilizando Android Studio en los siguientes dispositivos:
* Un portátil, una torre y el Mac de clase.
Lo aclaro, ya que los nombraré durante la práctica.

Las imágenes de este README están en la carpeta `ìmg-readme` así como el [vídeo](img-readme/Demo-editada.mp4) demostración.

La práctica también se puede descargar en [GitHub](https://github.com/solsolet/Filmoteca.git).

### Ejercicio 1
Primero he cambiado todo lo que se llama "_main_" y lo he sustituido por "_about_", p. ej. "`MainActivity.kt` -> `AboutActivity.kt`". Dentro de este archivo, quité también el "ViewCompat". 
Siguiendo con estos cambios también en el AndroidManifest he tenido cuidado que el archivo aparezca correctamente.

Por último, con el recurso `about_activity.xml` más de lo mismo.

### Ejercicio 2
Probarlo en el Mac ha sido sencillo, ha salido lo esperado.

Previamente, lo estaba probando con mi portátil y no me salía nada con el mismo código. Probé instalar HAXM pero no funcionó. Intenté también la conexión de mi móvil por USB con el portátil, y aunque la conexión funcionó, la ejecución de la aplicación no.

Sería cosa del portátil en sí, puede que tenga poca memoria para ejecutar la aplicación o me falte por descargar alguna cosa.

En casa, donde tengo una torre con un poco más de potencia, volví a probar y sí que fue. Como aclaración, el portátil y la torre tienen Linux, la distribución de Manjaro.
* Portátil: 8 GB de RAM.
* Torre: 16 GB de RAM.

### Ejercicio 3
Le he puesto los elementos comentados en el ejercicio, los he centrado con `constraitHorizontal` y los he espaciado. Los botones en `AboutActivity` los he nombrado como variables y luego les he añadido un _listener_ que al clicar muestre el _Toast_ de "_Funcionalidad sin implementar_".

Al probar la aplicación ha funcionado como se esperaba.

### Ejercicio 4
He seguido los pasos para la internacionalización. Al crear la nueva carpeta, `res/values-en`, veo que no se crea nada en Android Studio. Al preguntar el profe me comenta que es cosa de Mac: si creas una carpeta por defecto no aparece aunque esté.
La encuentro en el explorador y le añado un fichero para poder verla, en este caso una copia de `strings.xml`.

He usado la opción de Android Studio para traducir más cómodamente las _strings_, el editor de traducciones:
![Editor de Traducciones](img-readme/Translations-Editor.png)

Como detalle, destacaré que he marcado el título de la aplicación para que no pueda traducirse.

### Ejercicio 5
Este ejercicio lo encontré más complicado al haber leído solo por encima el apartado del libro que lo explicaba. Al principio, me peleé poniendo el código del enunciado sin mucha idea ni criterio, además de no saber muy bien qué hacía. Consulté de nuevo con el profesor, lo cual me esclareció bastante las cosas, aunque aún necesitaba una lectura más calmada.

Terminé por entender el funcionamiento y la refactorización del código. Hasta ahora, estábamos usando _Layouts_, así que ese código lo encapsularía en un apartado destinado a ello, y el resto sería cosa de _Compose_.

_Compose_ por su parte, a pesar de haber leído la documentación y más o menos entenderlo, no paraba de darme errores. Al principio pensaba que era por culpa del código añadido en `AboutActivity.kt`, pero el problema lo tenía con **gradle**.

Una vez solucionado, con seguir un poco la guía del libro y la documentación de _Jetpack_ ya lo tenía.

#### Gradle
Mi primer error fue suponer que _gradle_ tendría todo lo necesario puesto y que yo no tenía que añadir nada. Después de leer de nuevo el apartado de los apuntes donde se explica un poco más, veo que falta alguna línea de código, aunque mi código sigue sin funcionar.

Además de los apuntes, el propio Android, conforme añadía librerías, me sugería otras y marcaba errores que podría consultar en la web [Compose Compiler Gradle plugin](https://developer.android.com/develop/ui/compose/compiler).

El resultado fue una mezcla de librerías que tenía el proyecto de por sí, unidas con las que me hacían falta para todos los elementos que usaban _Compose_. Se pueden ver en el archivo `build.gradle.kts` (:app).

Al ver que funcionaban, no podía evitar fijarme en los numerosos _warnings_ que aparecían en algunas. Leyendo un poco, vi que algunas librerías hacían lo mismo que otras, por tanto, existía duplicidad y no se llegaban  a usar, y por eso refactoricé un poco.

### Errores
1. Primer día - Error al descargar Device: 
![error](img-readme/error.png)

Pudo haber sido por falta de internet o del internet tan lento de la uni. Al cambiarme al Mac de clase con el SSD pude aprovechar el tiempo que quedaba para hacer la actividad en clase. En casa intenté descargarlo a ver si era cosa de la mala conexión.

2. Cargado lento del dispositivo de prueba

De los 3 dispositivos que estoy programando, en aquellos con poca potencia se han vuelto tediosas las pruebas, tanto que parecía que tenía un error en Android Studio cuando no era culpa suya.
Un error que ha saltado en el portátil "Se ha evitado una escasez de memoria..." seguido del cierre de la aplicación. Para nada recomendable.

3. No hacer caso de los _warnings_

He encontrado muy útil el apartado `Problems > Project Errors` que te destaca los errores y _warnings_ del proyecto.
![Problems](img-readme/Problems.jpg)

Al curiosearla y ver _warnings_ no me había preocupado pero, revisando algunos con el profe, se me escapaban algunos como:
- no tener las librerías con la versión recomendada
- dejar cadenas de texto plano en lugar de su identificador `@string` en los elementos de la aplicación.

4. R.string.[algo] error _Compose_

Usar el identificador para cadenas de texto no funcionaba bien para los textos dentro de _Compose_. Se solucionó con la función `stringResource()`, que ciertamente no es muy intuitiva dado que en el resto del código lo puedes poner sin la función.

5. Falta de la imagen en el modo _Layout_

En las pruebas que he estado haciendo todos los elementos me salían bien, pero en las útlimas en las que estoy preparando la entrega y haciendo pequeños cambios no estaba comprobando el _Layout_ y para mi sorpresa he visto que la imagen no cargaba.
Revisando el código estaba todo como antes, así que no sé si es por culpa de alguna librería o línea de código que he cambiado.

Por lo que he estado debuggeando y leyendo, parece ser que al dividir la aplicación según el modo, _Layout_ decide el recurso en tiempo de ejecución que al ver que no està declarada en `AboutActivity.kt` explícitamente (como en los ejercicios sin la partición) no lo encuentra y, por tanto, no lo pinta.
Esto tambén explica por que en _Compose_ sí que aparece por que usa `painterResource` que lo pinta.

El código para `initLayouts` ha quedado así:
```kt
// Log.d("AboutActivity", "initLayouts: layout about assignat")
val imageView = findViewById<ImageView>(R.id.imageView2)
// Log.d("AboutActivity", "imageView is null? ${imageView == null}")
// imageView?.visibility = View.VISIBLE
imageView?.setImageResource(R.drawable.monito)
```
Donde los `Log` han servido para trazar el problema con _Logcat_. 

### Conclusiones
He aprendido mucho en esta práctica: todas las nociones de Gradle, consejos y pruebas creo que me dejan más preparada para la siguiente. Son bastantes conceptos nuevos y, con su uso, se me harán más intuitivos.

El tiempo que he tardado es el comprendido entre la clase del miércoles hasta el final de este viernes. Para ser la primera no sé si está bien o no, pero me tengo que dar un poco más de brío para que no se me acumule con las siguientes.

En cuanto a los **dispositivos** que he usado puedo clasificar su uso en:
|Dispositivo|Ventajas|Inconvenientes|
|-----------|--------|--------------|
|Portátil|Móvil: uso tanto en clase como en casa|Poca potencia, pantalla pequeña|
|Torre|Buen internet, 2 pantallas, potencia moderada|Solo puedo trabajar en casa|
|Mac|Buena pantalla, a veces va fluido|No estoy acostumbrada a Mac = lentitud, mal internet, a veces no va fluido|

La mejor opción para trabajar es la **torre**, así que intentaré hacer la mayor parte del trabajo en casa e intentaré que las pruebas en clase sean las menos posibles y me centre en dudas.

### Proyecto base
Después de que el profe subiese un proyecto base, he adaptado el mío para que se parezca lo máximo posible viendo poco a poco que decisiones de diseño ha tomado, modificar librerías y optimizar el código, sin olvidar por supuesto aprender. La parte que más ha cambiado es el modo _Layouts_ que ha pasado a ser _Bindings_ (y éste usa nuevas funciones para poner lo mismo).

## Navegación entre actividades
Siguiendo con la segunda pràctica de la sesión, ahora nos centraremos en añadirle contenido a la práctica que ya teníamos.

### Ejercicio 1
Le añadimos funcionalidad a los botones como se nos indica. Me surge la siguiente inquietud: si estamos usando Bindings y compose para hacer las mismas cosas, quiere decir que duplicaremos el mismo código contantemente si empezamos a añadir funcionalidad. Por tanto, todo aquello que es común (cada intent) lo saco en una función propia fuera de _initLayouts_ o _initCompose_:
```kt
private fun goWebsite(){
        val irWeb = Intent(Intent.ACTION_VIEW, "https://www.ua.es/va/".toUri())
        startActivity(irWeb)
    }
    private fun obtainSupport(){
        val soporte = Intent(Intent.ACTION_SENDTO, "mailto:gsl21@alu.ua.es".toUri())
        startActivity(soporte)
    }
    fun closeActivity(){ // No private por si la quisieramos usar fuera
        finish()
    }
```
Y luego en cada _init_ llamo a la función correspondiente:
|Binding|Compose|
|-------|-------|
|`bindings.button.setOnClickListener { goWebsite() }`|`Button(onClick = {  goWebsite() }) { Text(stringResource(R.string.button)) }`|
#### Ojito
En el modo _Compose_ he intentado quitar los corchetes del `onclick = {}` para dejar el método suelto, pero me ha dado error. Vista la ayuda de Android Studio veo lo siguiente: 
![Compose: Button onClick function, parámetro Unit](img-readme/Compose-onClick-Unit.png)

Más adelante podría investigar sobre esto a ver si hay alguna otra manera de ponerlo y ver si es mejor.

### Ejercicio 2
Creamos las nuevas Actividades y cambiamos de aplicación principal:
![FilmActivities](img-readme/FilmActivities-Manifest.png)

Primero ponemos todos los elementos en cada actividad de manera que resultan así (aunque esten un poco feos):
|Pantalla|Compose|Binding|
|--------|-------|-------|
|FilmList|![Pantalla Compose FilmList](img-readme/ComposeFilmList.png)|![Pantalla Binding FilmList](img-readme/BindingFilmList.png)|
|FilmData|![Pantalla Compose FilmData](img-readme/ComposeFilmData.png)|![Pantalla Binding FilmData](img-readme/BindingFilmData.png)|
|FilmEdit|![Pantalla Compose FilmEdit](img-readme/ComposeFilmEdit.png)|![Pantalla Binding FilmEdit](img-readme/BindingFilmEdit.png)|
|About|![Pantalla Compose About](img-readme/ComposeAbout.png)|![Pantalla Binding About](img-readme/BindingAbout.png)|
### Ejercicio 3