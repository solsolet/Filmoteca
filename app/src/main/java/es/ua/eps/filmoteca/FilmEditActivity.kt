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
    private fun guardar() {
        val res = Intent()
        res.putExtra(EXTRA_FILM_TITLE, "editado")
        setResult(RESULT_OK, res)
        finish()
    }
    private fun initLayouts() {
        bindings = ActivityFilmEditBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)
            bindings.guardar.setOnClickListener { guardar() }
            bindings.cancelar.setOnClickListener { cerrar() }
        }
    }
    private fun initCompose() { //no se què fer ací
        setContent {
            MaterialTheme {
                ComposableFilmEdit()
            }
        }
    }
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
            Button(onClick = { //TODO: check Unit thing
                guardar()
            }) {
                Text(stringResource(R.string.guardar))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                cerrar()
            }) {
                Text(stringResource(R.string.cancelar))
            }
        }
    }
}