package es.ua.eps.filmoteca

object FilmDataSource {
    val films: MutableList<Film> = mutableListOf<Film>()

    init {
        var f = Film()
        f.title = "Regreso al futuro"
        f.director = "Robert Zemeckis"
        f.imageResId = R.mipmap.ic_launcher
        f.comments = ""
        f.format = Film.Companion.FORMAT_DIGITAL
        f.genre = Film.Companion.GENRE_SCIFI
        f.imdbUrl = "http://www.imdb.com/title/tt0088763"
        f.year = 1985
        films.add(f)

        // Añade tantas películas como quieras!
        f = Film()
        f.title = "La La Land"
        f.director = "Damien Chazelle"
        f.imageResId = R.drawable.lalaland
        f.comments = ""
        f.format = Film.Companion.FORMAT_DIGITAL
        f.genre = Film.Companion.GENRE_COMEDY
        f.imdbUrl = "https://www.imdb.com/title/tt3783958/?ref_=fn_all_ttl_1"
        f.year = 2017
        films.add(f)

        f = Film()
        f.title = "Bad Guys 2"
        f.director = "Pierre Perifel"
        f.imageResId = R.drawable.bad_guys2
        f.comments = ""
        f.format = Film.Companion.FORMAT_DIGITAL
        f.genre = Film.Companion.GENRE_COMEDY
        f.imdbUrl = "https://www.imdb.com/title/tt30017619/"
        f.year = 2025
        films.add(f)

        f = Film()
        f.title = "Mickey 17"
        f.director = "Bong Joon Ho"
        f.imageResId = R.drawable.mickey17
        f.comments = ""
        f.format = Film.Companion.FORMAT_DVD
        f.genre = Film.Companion.GENRE_SCIFI
        f.imdbUrl = "https://www.imdb.com/title/tt12299608/"
        f.year = 2025
        films.add(f)

        f = Film()
        f.title = "K-pop Demon Hunters"
        f.director = "Maggie Kang"
        f.imageResId = R.drawable.kpop_demon_hunters
        f.comments = ""
        f.format = Film.Companion.FORMAT_DIGITAL
        f.genre = Film.Companion.GENRE_ACTION
        f.imdbUrl = "https://www.imdb.com/title/tt14205554/"
        f.year = 2025
        films.add(f)
    }
}