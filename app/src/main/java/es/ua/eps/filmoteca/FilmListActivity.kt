package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private fun initLayouts() {
        bindings = ActivityFilmListBinding.inflate(layoutInflater)

        val filmList = FilmDataSource.films

        with(bindings) {
            setContentView(root)
            // ListView
//            val adaptador = FilmsArrayAdapter(
//                this,
//                R.layout.item_peli, filmList
//            )
//            pelisList.setOnItemClickListener({ parent: AdapterView<*>, view: View, position: Int, id: Long ->
//                verPeli(position) //Intent
//            })
//            pelisList.adapter = adaptador

            // RecyclerView
            val adaptador = FilmsAdapter(filmList) { //TODO(canviar pulsar, mirar apunts listas final)
                position -> verPeli(position)
            }
            val recyclerView: RecyclerView = findViewById(R.id.recyclerviewPelis)
            recyclerView.layoutManager = LinearLayoutManager(this@FilmListActivity)

            recyclerView.adapter = adaptador
        }
    }
    private fun initCompose() {
        setContent {
            MaterialTheme {
                ComposableFilmList(
//                    onFilmClick = { peliIndex ->
//                        verPeli(peliIndex)
//                    }
                )
            }
        }
    }
    @Composable
    private fun ComposableFilmList() {
        val context = LocalContext.current
        val films = FilmDataSource.films

        LazyColumn (
            modifier = Modifier
                .padding(15.dp)
        ){
//            items(films, key = {it.hashCode()}) { peli ->
//                FilmItem(peli)
//            }
            itemsIndexed(films){ index, peli ->
                FilmItem(
                    f = peli
                )
            }
        }
    }
    @Composable
    fun FilmItem(f: Film) {
        // == layout XML + bind ViewHolder
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable{ verPeli(0)}
                .padding(8.dp)
        ) {
            val imageId = if (f.imageResId != 0) f.imageResId else R.drawable.default_movie
            Image(
                painter = painterResource(id = imageId),
                contentDescription = f.title,
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 12.dp)
            )
            Column {
                f.title?.let { Text(
                    text = it,
                    fontSize = 20.sp
                ) }
                f.director?.let { Text(
                    text = it,
                    fontStyle = FontStyle.Italic
                ) }
            }
        }
    }
}