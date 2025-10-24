package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.ua.eps.filmoteca.FilmDataActivity.Companion.EXTRA_FILM_TITLE
import es.ua.eps.filmoteca.FilmDataActivity.Companion.EXTRA_FILM
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
    private fun verPeli(peli: Int){
        val verPeli = Intent(this@FilmListActivity, FilmDataActivity::class.java)
        verPeli.putExtra(EXTRA_FILM, peli)
        startActivity(verPeli)
    }
//    private fun acercaDe(){
//        val about = Intent(this@FilmListActivity, AboutActivity::class.java)
//        startActivity(about)
//    }
    private fun initLayouts() {
        bindings = ActivityFilmListBinding.inflate(layoutInflater)

        val valores = FilmDataSource.films

        val adaptador = FilmsArrayAdapter(
            this,
            R.layout.item_peli, valores
        )

        with(bindings) {
            setContentView(root)
            pelisList.setOnItemClickListener({ parent: AdapterView<*>, view: View, position: Int, id: Long ->
                val elemento = adaptador.getItem(position)
//                Toast.makeText(
//                    this@FilmListActivity,
//                    "$elemento seleccionado: id $id, position $position", Toast.LENGTH_LONG
//                ).show()
                verPeli(position) //Intent
            })
            pelisList.adapter = adaptador
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
        val films = FilmDataSource.films

        Column( //equivalent a LinearLayout(vertical)
            modifier = Modifier
                .fillMaxSize()      // separated like class notes' style
                .padding(64.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {

            }
        }
    }
}