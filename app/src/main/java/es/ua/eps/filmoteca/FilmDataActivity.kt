package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
    companion object {
        val EXTRA_FILM_TITLE = "EXTRA_FILM_TITLE"
    }
    // Centralitzem Intents
    private fun verPeliRel(titol: String){
        val verPeliRel = Intent(this@FilmDataActivity, FilmDataActivity::class.java)
        verPeliRel.putExtra(EXTRA_FILM_TITLE, titol)
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
    private fun initLayouts() {
        bindings = ActivityFilmDataBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)

            //mostrar títol peli
            val peli = intent.getStringExtra(EXTRA_FILM_TITLE) ?: getString(R.string.tituloPeliDefecto)
            bindings.textViewTituloPeli.text = peli

            bindings.verPeliRel.setOnClickListener { verPeliRel(peli) }
            bindings.editPeli.setOnClickListener { editPeli() }
            bindings.volverPrincipal.setOnClickListener { volverPrinc() }
        }
    }
    private fun initCompose() { //no se què fer ací
        setContent {
            MaterialTheme {
                ComposeFilmData()
            }
        }
    }
    @Composable
    private fun ComposeFilmData() {
        val context = LocalContext.current
        val peli = intent.getStringExtra(EXTRA_FILM_TITLE) ?: getString(R.string.tituloPeliDefecto)

        Column( //equivalent a LinearLayout(vertical)
            modifier = Modifier
                .fillMaxSize()      // separat com posa en els apunts
                .padding(64.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(peli)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { //TODO: mirar lo de Unit
                verPeliRel(peli)
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