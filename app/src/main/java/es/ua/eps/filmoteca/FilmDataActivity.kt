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
import androidx.core.net.toUri
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding

class FilmDataActivity : AppCompatActivity() {
    private lateinit var bindings : ActivityFilmDataBinding
    var textEditado = ""

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
        const val EXTRA_FILM_TITLE = "EXTRA_FILM_TITLE"
    }
    //
    //  Centralize Intents
    private fun verPeliIMDB(){
        val peliIMDB = Intent(Intent.ACTION_VIEW, "https://www.imdb.com/title/tt3783958/?ref_=ext_shr_lnk".toUri())
        startActivity(peliIMDB)
    }
    val CODIGO_ACTIVIDAD_EDITAR = 1
    private fun editPeli(){
        val edit = Intent(this@FilmDataActivity, FilmEditActivity::class.java)
        startActivityForResult(edit, CODIGO_ACTIVIDAD_EDITAR)
    }
    // called when secondary activity finishes
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODIGO_ACTIVIDAD_EDITAR){
            if (resultCode == RESULT_OK){
                textEditado = getString(R.string.txtEditado)
                when (Filmoteca.GlobalMode) {
                    Mode.Bindings -> refreshTitleBinding()
                    Mode.Compose -> refreshCompose()
                }
            }
        }
    }
    fun refreshTitleBinding(){
        bindings.textViewTituloPeli.append(textEditado)
    }

    private fun volverPrinc(){
        val back = Intent(this@FilmDataActivity, FilmListActivity::class.java)
        back.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(back)
    }
    private fun initLayouts() {
        bindings = ActivityFilmDataBinding.inflate(layoutInflater)
        with(bindings) { //saves writing "binding." before
            setContentView(root)

            //shows film title
            val peli = intent.getStringExtra(EXTRA_FILM_TITLE) ?: getString(R.string.tituloPeli)
            textViewTituloPeli.text = peli

            verPeliIMDB.setOnClickListener { verPeliIMDB() }
            editPeli.setOnClickListener { editPeli() }
            volverPrincipal.setOnClickListener { volverPrinc() }
        }
    }
    private fun initCompose() {
        setContent {
            MaterialTheme {
                ComposeFilmData()
            }
        }
    }
    @Composable
    private fun ComposeFilmData() {
        val context = LocalContext.current
        val peli = intent.getStringExtra(EXTRA_FILM_TITLE) ?: getString(R.string.tituloPeli)

        Column( //equivalent a LinearLayout(vertical)
            modifier = Modifier
                .fillMaxSize()      // separated, class notes' style
                .padding(64.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(peli + textEditado)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { //TODO: check Unit thing
                verPeliIMDB()
            }) {
                //Text(stringResource(R.string.verPeliIMDB))
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
    private fun refreshCompose() {
        setContent {
            MaterialTheme {
                ComposeFilmData()
            }
        }
    }
}