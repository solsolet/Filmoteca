package es.ua.eps.filmoteca

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.FilmDataActivity.Companion.EXTRA_FILM
import es.ua.eps.filmoteca.databinding.ActivityFilmEditBinding

class FilmEditActivity : AppCompatActivity() {
    private lateinit var bindings : ActivityFilmEditBinding

    companion object {
        // Single source of truth for the key — both callers import this
        const val EXTRA_FILM_INDEX = "EXTRA_FILM_INDEX_EDIT"
    }

    // MARK: LAUNCHERS + PERMISSIONS
    private val fineLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (fineGranted) {
            // Fine location granted — now ask for background separately (Android 10+ only)
            backgroundLocationLauncher.launch(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            Toast.makeText(
                this,
                "Fine location permission is required to set geofences",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val backgroundLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { backgroundGranted ->
        if (backgroundGranted) {
            addGeofenceForCurrentFilm()
        } else {
            // Background location denied — on Android 11+ the user has to go to Settings
            // to grant "Allow all the time". We show a helpful message.
            Toast.makeText(
                this@FilmEditActivity,
                "Please allow location 'All the time' in Settings for geofences to work in background",
                Toast.LENGTH_LONG
            ).show()
            // We still try to add the geofence — it will work when app is in foreground at least
            addGeofenceForCurrentFilm()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initUI()
        //App bar
        setSupportActionBar(findViewById(R.id.mtHomeMenu))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mtHomeMenu)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }
    }
    private fun initUI() {
        when (Filmoteca.GlobalMode) {
            Mode.Bindings -> initLayouts()
            Mode.Compose -> initCompose()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) { // ID special for "home"
            NavUtils.navigateUpTo(this@FilmEditActivity,
                Intent(this@FilmEditActivity, FilmListActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
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
        val peliInt = intent.getIntExtra(EXTRA_FILM_INDEX, -1) //get ID
        updateFilm(peliInt, titulo, director, any, imdb, generoIndex, formatoIndex, comentarios)

        val res = Intent()
        res.putExtra(EXTRA_FILM, peliInt)
        setResult(RESULT_OK, res)
        finish()
    }

    // MARK: LAYOUT
    private fun initLayouts() {
        bindings = ActivityFilmEditBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)

            val peliInt = intent.getIntExtra(EXTRA_FILM_INDEX, -1) //get ID
            val peli = FilmDataSource.films[peliInt]

            imageViewPosterEditar.setImageResource(peli.imageResId)
            spinnerGenero.setSelection(peli.genre)
            spinnerFormato.setSelection(peli.format)

            // Location
            btnAddGeofence.setOnClickListener {
                // Check if the film has coordinates first
                if (!peli.hasLocation) {
                    Toast.makeText(this@FilmEditActivity, "Set latitude and longitude first", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                requestLocationPermissionsAndAddGeofence()
            }

            btnRemoveGeofence.setOnClickListener {
                GeofenceManager.removeGeofence(
                    context = this@FilmEditActivity,
                    film = peli,
                    onSuccess = {
                        Toast.makeText(this@FilmEditActivity, "Geofence removed", Toast.LENGTH_SHORT).show()
                        updateGeofenceButtons()
                    },
                    onFailure = { error ->
                        Toast.makeText(this@FilmEditActivity, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                )
            }

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

            updateGeofenceButtons()
        }
    }

    // MARK: LOCATION

    /**
     * Shows/hides each button depending on whether the film already has a geofence.
     * This gives the user clear feedback about the current state.
     */
    private fun updateGeofenceButtons() {
        val peliInt = intent.getIntExtra(EXTRA_FILM_INDEX, -1) //get ID
        val peli = FilmDataSource.films[peliInt]

        if (peli.hasGeofence) {
            bindings.btnAddGeofence.visibility = View.GONE
            bindings.btnRemoveGeofence.visibility = View.VISIBLE
        } else {
            bindings.btnAddGeofence.visibility = View.VISIBLE
            bindings.btnRemoveGeofence.visibility = View.GONE
        }
    }

    /**
     * Checks permissions before adding a geofence.
     *
     * On Android 10+ background location is a SEPARATE permission that must be
     * requested AFTER fine location is already granted — the system won't show
     * both dialogs at the same time. So we request fine location first; if it's
     * already granted we request background location as a second step.
     */
    private fun requestLocationPermissionsAndAddGeofence() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this@FilmEditActivity, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundGranted =
            ContextCompat.checkSelfPermission(
                this@FilmEditActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        when {
            // Everything already granted — go directly
            fineGranted && backgroundGranted -> addGeofenceForCurrentFilm()

            // Fine not granted yet — start the two-step flow
            !fineGranted -> fineLocationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            // Fine granted but background not yet — ask for background
            else -> backgroundLocationLauncher.launch(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    private fun addGeofenceForCurrentFilm() {
        val peliInt = intent.getIntExtra(EXTRA_FILM_INDEX, -1) //get ID
        val peli = FilmDataSource.films[peliInt]

        GeofenceManager.addGeofence(
            context = this@FilmEditActivity,
            film = peli,
            onSuccess = {
                Toast.makeText(this@FilmEditActivity, "Geofence added!", Toast.LENGTH_SHORT).show()
                updateGeofenceButtons()
            },
            onFailure = { error ->
                Toast.makeText(this@FilmEditActivity, "Error adding geofence: $error", Toast.LENGTH_LONG).show()
            }
        )
    }

    // MARK: COMPOSE
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
        val peliInt = intent.getIntExtra(EXTRA_FILM_INDEX, -1)
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