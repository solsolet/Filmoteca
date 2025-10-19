package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import es.ua.eps.filmoteca.FilmDataActivity.Companion.EXTRA_FILM_TITLE
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding

class FilmListActivity : ComponentActivity() {
    private lateinit var bindings : ActivityFilmListBinding

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
    private fun verPeli(titol: String){
        val verPeli = Intent(this@FilmListActivity, FilmDataActivity::class.java)
        verPeli.putExtra(EXTRA_FILM_TITLE, titol)
        startActivity(verPeli)
    }
    private fun acercaDe(){
        val about = Intent(this@FilmListActivity, AboutActivity::class.java)
        startActivity(about)
    }
    private fun initLayouts() {
        bindings = ActivityFilmListBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)
            bindings.verPeliA.setOnClickListener { verPeli(getString(R.string.tituloPeliA)) }
            bindings.verPeliB.setOnClickListener { verPeli(getString(R.string.tituloPeliB)) }
            bindings.acercaDe.setOnClickListener { acercaDe() }
        }
    }
    private fun initCompose() { //no se què fer ací
        setContent {
            MaterialTheme {
                ComposableFilmList()
            }
        }
    }
    @Composable
    private fun ComposableFilmList() {
        val context = LocalContext.current
        val titolPeliA = stringResource(R.string.tituloPeliA)
        val titolPeliB = stringResource(R.string.tituloPeliB)

        Column( //equivalent a LinearLayout(vertical)
            modifier = Modifier
                .fillMaxSize()      // separated like class notes' style
                .padding(64.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { //TODO: mirar lo de Unit
                verPeli(titolPeliA)
            }) {
                Text(stringResource(R.string.verPeliA))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                verPeli(titolPeliB)
            }) {
                Text(stringResource(R.string.verPeliB))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                acercaDe()
            }) {
                Text(stringResource(R.string.acercaDe))
            }
        }
    }
}