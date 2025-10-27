package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import es.ua.eps.filmoteca.FilmDataActivity.Companion.EXTRA_FILM
import es.ua.eps.filmoteca.databinding.ActivityFilmEditBinding

class FilmEditActivity : AppCompatActivity() {
    private lateinit var bindings : ActivityFilmEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initUI()
    }
    private fun initUI() {
        when (Filmoteca.GlobalMode) {
            Mode.Bindings -> initLayouts()
            Mode.Compose -> initCompose()
        }
    }
    private fun updateFilm(
        peliInt: Int,
        titulo: String?,
        director: String?,
        any: String?,
        imdb: String?,
        generoIndex: Int?,
        formatoIndex: Int?,
        comentarios: String?
    ){
        val peli = FilmDataSource.films[peliInt]

        if (!titulo.isNullOrBlank()) peli.title = titulo
        if (!director.isNullOrBlank()) peli.director = director
        if (!any.isNullOrBlank()) peli.year = any.toIntOrNull() ?: peli.year
        if (!imdb.isNullOrBlank()) peli.imdbUrl = imdb
        if (generoIndex != null) peli.genre = generoIndex
        if (formatoIndex != null) peli.format = formatoIndex
        if (!comentarios.isNullOrBlank()) peli.comments = comentarios
    }
    // Centralize Intents
    private fun cerrar() {
        setResult(RESULT_CANCELED, null)
        finish()
    }
    private fun guardar(
        titulo: String?,
        director: String?,
        any: String?,
        imdb: String?,
        generoIndex: Int?,
        formatoIndex: Int?,
        comentarios: String?
    ) {
        val peliInt = intent.getIntExtra(EXTRA_FILM, 0) //get ID
        updateFilm(peliInt, titulo, director, any, imdb, generoIndex, formatoIndex, comentarios)

        val res = Intent()
        res.putExtra(EXTRA_FILM, peliInt)
        setResult(RESULT_OK, res)
        finish()
    }
    private fun initLayouts() {
        bindings = ActivityFilmEditBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)

            val peliInt = intent.getIntExtra(EXTRA_FILM, 0) //get ID
            val peli = FilmDataSource.films[peliInt]

            imageViewPosterEditar.setImageResource(peli.imageResId)
            spinnerGenero.setSelection(peli.genre)
            spinnerFormato.setSelection(peli.format)

            guardar.setOnClickListener {
                val titulo = editTitulo.text.toString().trim()
                val director = editDirector.text.toString().trim()
                val any = editAny.text.toString().trim()
                val imdb = editIMDB.text.toString().trim()
                val comentarios = editNotas.text.toString().trim()
                val generoIndex = spinnerGenero.selectedItemPosition
                val formatoIndex = spinnerFormato.selectedItemPosition

                guardar(titulo, director, any, imdb, generoIndex, formatoIndex, comentarios)
            }
            cancelar.setOnClickListener { cerrar() }
        }
    }
    private fun initCompose() {
        setContent {
            MaterialTheme {
                ComposableFilmEdit()
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ComposableFilmEdit() {
        val context = LocalContext.current
        val peliInt = intent.getIntExtra(EXTRA_FILM, 0)
        val peli = FilmDataSource.films[peliInt]

        var titulo by remember { mutableStateOf(peli.title ?: "") }
        var director by remember { mutableStateOf(peli.director ?: "") }
        var any by remember { mutableStateOf(peli.year.toString()) }
        var imdb by remember { mutableStateOf(peli.imdbUrl ?: "") }
        var comentarios by remember { mutableStateOf(peli.comments ?: "") }
        var generoIndex by remember { mutableIntStateOf(peli.genre) }
        var formatoIndex by remember { mutableIntStateOf(peli.format) }

        // "Spinners"
        val genPeli = stringResource(R.string.generoPeli)
        var generoExpanded by remember { mutableStateOf(false) }
        var selectedGenero by remember { mutableStateOf(genPeli) }

        val forPeli = stringResource(R.string.formatoPeli)
        var formatoExpanded by remember { mutableStateOf(false) }
        var selectedFormato by remember { mutableStateOf(forPeli) }

        val generos = context.resources.getStringArray(R.array.generoPeli).toList()
        val formatos = context.resources.getStringArray(R.array.formatoPeli).toList()


        Column( //equivalent a LinearLayout(vertical)
            modifier = Modifier
                .fillMaxSize()      // separated, class notes' style
                .padding(64.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.editandoPeli))
            Row (
                modifier = Modifier
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Image(
                    painter = painterResource(peli.imageResId),
                    contentDescription = peli.title,
                    modifier = Modifier
                        .width(65.dp)
                        .height(70.dp)
                )
                Button(onClick = {
                    //
                }) {
                    Text(stringResource(R.string.captFoto))
                }
                Button(onClick = {
                    //
                }) {
                    Text(stringResource(R.string.selectImg))
                }
            }
            TextField(
                value = titulo,
                onValueChange = { titulo = it },
                //state = rememberTextFieldState(),
                label = { Text(stringResource(R.string.tituloPeli)) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextField(
                value = director,
                onValueChange = { director = it },
                label = { Text(stringResource(R.string.directorPeli)) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextField(
                value = any,
                onValueChange = { any = it },
                label = { Text(stringResource(R.string.anyPeli)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextField(
                value = imdb,
                onValueChange = { imdb = it },
                label = { Text(stringResource(R.string.enlaceIMDB)) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            //"Spinner" Genero/Formato
            ExposedDropdownMenuBox(
                expanded = generoExpanded,
                onExpandedChange = { generoExpanded = !generoExpanded }
            ) {
                TextField(
                    value = generos[generoIndex],
                    onValueChange = {},
                    label = { Text(stringResource(R.string.generoPeli)) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = generoExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = generoExpanded,
                    onDismissRequest = { generoExpanded = false }
                ) {
                    generos.forEachIndexed { index, genero ->
                        DropdownMenuItem(
                            text = { Text(genero) },
                            onClick = {
                                generoIndex = index
                                generoExpanded = false
                            }
                        )
                    }
                }
            }
            // Formato
            ExposedDropdownMenuBox(
                expanded = formatoExpanded,
                onExpandedChange = { formatoExpanded = !formatoExpanded }
            ) {
                TextField(
                    value = formatos[formatoIndex],
                    onValueChange = {},
                    label = { Text(stringResource(R.string.formatoPeli)) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatoExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = formatoExpanded,
                    onDismissRequest = { formatoExpanded = false }
                ) {
                    formatos.forEachIndexed { index, formato ->
                        DropdownMenuItem(
                            text = { Text(formato) },
                            onClick = {
                                formatoIndex = index
                                formatoExpanded = false
                            }
                        )
                    }
                }
            }
            TextField(
                value = comentarios,
                onValueChange = { comentarios = it },
                label = { Text(stringResource(R.string.notasPeli)) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Row (
                modifier = Modifier
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Button(onClick = {
                    guardar(titulo, director, any, imdb, generoIndex, formatoIndex, comentarios)
                }) {
                    Text(stringResource(R.string.guardar))
                }
                Button(onClick = {
                    cerrar()
                }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        }
    }
}