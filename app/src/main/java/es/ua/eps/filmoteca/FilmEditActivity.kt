package es.ua.eps.filmoteca

import android.R.attr.enabled
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import es.ua.eps.filmoteca.FilmDataActivity.Companion.EXTRA_FILM_TITLE
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
    // Centralize Intents
    private fun cerrar() {
        setResult(RESULT_CANCELED, null)
        finish()
    }
    private fun guardar(peliInt: Int) {
        val peli = FilmDataSource.films[peliInt]

        bindings = ActivityFilmEditBinding.inflate(layoutInflater)
        with(bindings) {
            // Update film
//            if (TextUtils.isEmpty(editTitulo.getText())){
//                Toast.makeText(this@FilmEditActivity, "You did not enter a username", Toast.LENGTH_SHORT).show();
//            } else {
                peli.title = editTitulo.text.toString() // OG que sí que va soles
//            }

//            val titulo = editTitulo.text.toString().trim()
//            val director = editDirector.text.toString().trim()
//            val any = editAny.text.toString().trim()
//            val comentarios = editNotas.text.toString().trim()
//
//            // Solo actualizamos los campos que NO estén vacíos
//            if (titulo.isNotEmpty()) peli.title = titulo
//            if (director.isNotEmpty()) peli.director = director
//            if (any.isNotEmpty()) peli.year = any.toIntOrNull() ?: peli.year
//            if (comentarios.isNotEmpty()) peli.comments = comentarios
        }

        //TODO POSAR BE

        val res = Intent()
        //res.putExtra(EXTRA_FILM_TITLE, "editado")
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

            val tituloEdit =  editTitulo.getText().toString()
//            if (tituloEdit.trim().equals("")){
//                Toast.makeText(this@FilmEditActivity, "You did not enter a username", Toast.LENGTH_SHORT).show();
//            }

            guardar.setOnClickListener {
                if (TextUtils.isEmpty(editTitulo.getText())){
                    Toast.makeText(this@FilmEditActivity, "You did not enter a username", Toast.LENGTH_SHORT).show();
                } else {
                    guardar(peliInt)
                }
                //guardar(peliInt)

            }
            cancelar.setOnClickListener { cerrar() }
        }
    }
    private fun initCompose() { //no se què fer ací
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
                    painter = painterResource(R.drawable.lalaland),
                    contentDescription = stringResource(R.string.contentImage),
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
                state = rememberTextFieldState(),
                label = { Text(stringResource(R.string.tituloPeli)) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextField(
                state = rememberTextFieldState(),
                label = { Text(stringResource(R.string.directorPeli)) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextField(
                state = rememberTextFieldState(),
                label = { Text(stringResource(R.string.anyPeli)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextField(
                state = rememberTextFieldState(),
                label = { Text(stringResource(R.string.enlaceIMDB)) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            //Spinner more or less
            var genPeli = stringResource(R.string.generoPeli)
            var generoExpanded by remember { mutableStateOf(false) }
            var selectedGenero by remember { mutableStateOf(genPeli) }

            var forPeli = stringResource(R.string.formatoPeli)
            var formatoExpanded by remember { mutableStateOf(false) }
            var selectedFormato by remember { mutableStateOf(forPeli) }

            val generos = listOf(R.array.generoPeli)
            val formatos = listOf(R.array.formatoPeli)

            ExposedDropdownMenuBox(
                expanded = generoExpanded,
                onExpandedChange = { generoExpanded = !generoExpanded }

            ) {
                TextField(
                    value = selectedGenero,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.generoPeli)) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatoExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = generoExpanded,
                    onDismissRequest = { generoExpanded = false }
                ) {
                    generos.forEach { genero ->
                        DropdownMenuItem(
                            text = { Text(genero.toString()) },
                            onClick = {
                                selectedGenero = genero.toString()
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
                    value = selectedFormato,
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
                    formatos.forEach { formato ->
                        DropdownMenuItem(
                            text = { Text(formato.toString()) },
                            onClick = {
                                selectedFormato = formato.toString()
                                formatoExpanded = false
                            }
                        )
                    }
                }
            }
            TextField(
                state = rememberTextFieldState(),
                label = { Text(stringResource(R.string.notasPeli)) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Row (
                modifier = Modifier
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Button(onClick = { //TODO: check Unit thing
                    guardar(0)
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