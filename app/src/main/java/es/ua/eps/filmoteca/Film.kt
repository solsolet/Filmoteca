package es.ua.eps.filmoteca

class Film {
    var title: String? = null
    var director: String? = null
    var year: Int = 0
    var genre: Int = 0
    var format: Int = 0
    var imdbUrl: String? = null
    var imageResId: Int = 0 // Propiedades de la clase
    var comments: String? = null

    var latitude: Double = 0.0 // Coordinates of the filming location
    var longitude: Double = 0.0

    var hasGeofence: Boolean = false // Whether a geofence is currently registered for this film

    val hasLocation: Boolean // Helper to know if a valid location has been set
        get() = latitude != 0.0 || longitude != 0.0


    override fun toString(): String {
        return title?:"<Sin titulo>" // Al convertir a cadena mostramos su título
    }

    companion object {
        const val FORMAT_DVD = 0 // Formatos
        const val FORMAT_BLURAY = 1
        const val FORMAT_DIGITAL = 2
        const val GENRE_ACTION = 0 // Géneros
        const val GENRE_COMEDY = 1
        const val GENRE_DRAMA = 2
        const val GENRE_SCIFI = 3
        const val GENRE_HORROR = 4
    }
}