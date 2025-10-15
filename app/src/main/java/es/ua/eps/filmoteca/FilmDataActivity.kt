package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding

class FilmDataActivity : AppCompatActivity() {
    private lateinit var bindings : ActivityFilmDataBinding

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
    // Centralitzem Intents
    private fun verPeliRel(titol: String){
        val verPeliRel = Intent(this@FilmDataActivity, FilmDataActivity::class.java)
        verPeliRel.putExtra(FilmDataActivity.Extras.EXTRA_FILM_TITLE, titol)
        startActivity(verPeliRel)
    }
    private fun editPeli(){
        val edit = Intent(this@FilmDataActivity, FilmEditActivity::class.java)
        startActivity(edit)
    }
    private fun volverPrinc(){
        val volver = Intent(this@FilmDataActivity, FilmListActivity::class.java)
        volver.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(volver)
    }
    // Companion Object
    companion object Extras {
        const val EXTRA_FILM_TITLE = "EXTRA_FILM_TITLE"
    }
    private fun initLayouts() {
        bindings = ActivityFilmDataBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)
            //veem parametre
            val peli = intent.getStringExtra(EXTRA_FILM_TITLE) ?: getString(R.string.tituloPeliDefault)
            bindings.textViewTitulo.text = "Datos $peli" //TODO vigilar internacionalitzacio

            bindings.verPeliRel.setOnClickListener { verPeliRel(peli) }
            bindings.editPeli.setOnClickListener { editPeli() }
            bindings.volverPrincipal.setOnClickListener { volverPrinc() }
        }
    }
    private fun initCompose() { //no se què fer ací
        val peli = intent.getStringExtra(EXTRA_FILM_TITLE) ?: getString(R.string.tituloPeliDefault)

        setContent {
            MaterialTheme {
                FilmDataCompose(peli)
            }
        }
    }
    @Composable
    private fun FilmDataCompose(titol: String) {
        val context = LocalContext.current

        Column( //equivalent a LinearLayout(vertical)
            modifier = Modifier
                .fillMaxSize()      // separat com posa en els apunts
                .padding(64.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Mostrant: $titol")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { //TODO: mirar lo de Unit
                verPeliRel(titol)
            }) {
                Text(stringResource(R.string.verPeliRel))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                editPeli()
            }) {
                Text(stringResource(R.string.editPeli))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                volverPrinc()
            }) {
                Text(stringResource(R.string.volverPrincipal))
            }
        }
    }
}