package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView.MultiChoiceModeListener
import android.widget.AdapterView
import android.widget.ListView
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.ua.eps.filmoteca.FilmDataActivity.Companion.EXTRA_FILM
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding

class FilmListActivity : AppCompatActivity() {
    private lateinit var bindings : ActivityFilmListBinding
    private val filmList = FilmDataSource.films
    private lateinit var adaptador : FilmsArrayAdapter // ListView
    //private lateinit var adaptador : FilmsAdapter // RecyclerView

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
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.film_list_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.miNewFilm -> {
                newFilm()
                return true
            }
            R.id.miShowAbout -> {
                openAbout()
                return true
            }
        }
        return false
    }
    private fun openAbout() {
        val openA = Intent(this@FilmListActivity, AboutActivity::class.java)
        startActivity(openA)
    }
    private fun newFilm() {
        val f = Film()
        f.title = "<New film>"
        f.imageResId = R.mipmap.ic_launcher
        FilmDataSource.films.add(f)
        adaptador.notifyDataSetChanged()
    }
    private fun deleteSelectedFilm() {
        bindings.pelisList.let {
            val indices = it.checkedItemPositions
            val toDelete: MutableList<Film> = ArrayList()
            for (i in 0 until indices.size()) {
                if (indices.valueAt(i)) {
                    toDelete.add(filmList[indices.keyAt(i)])
                }
            }
            filmList.removeAll(toDelete)
            adaptador.notifyDataSetChanged()
        }
        //bindings.pelisList.clearChoices()
    }
    private fun selectMultipleFilm(){
        bindings.pelisList.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        bindings.pelisList.setMultiChoiceModeListener(
            object : MultiChoiceModeListener {
                override fun onCreateActionMode( mode: ActionMode, menu: Menu
                ) : Boolean {
                    val inflater = mode.menuInflater
                    inflater.inflate(R.menu.film_list_contextual_menu, menu)
                    return true
                }
                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }
                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.miDelete -> {
                            deleteSelectedFilm()
                            mode.finish()
                            true
                        }
                        else -> false
                    }
                }
                override fun onDestroyActionMode(mode: ActionMode) {}
                override fun onItemCheckedStateChanged(
                    mode: ActionMode, position: Int, id: Long, checked: Boolean) {
                    val count = bindings.pelisList.checkedItemCount
                    mode.title = "$count ${getString(R.string.multipleMenuCountSelected)}"
                }
            })
    }
    private fun initLayouts() {
        bindings = ActivityFilmListBinding.inflate(layoutInflater)

        with(bindings) {
            setContentView(root)
            setSupportActionBar(findViewById(R.id.mtMenu)) // Adds app bar
            // ListView
            adaptador = FilmsArrayAdapter(
                this@FilmListActivity,
                R.layout.item_peli, filmList
            )
            pelisList.setOnItemClickListener({ parent: AdapterView<*>, view: View, position: Int, id: Long ->
                verPeli(position) //Intent
            })
            pelisList.adapter = adaptador

            selectMultipleFilm()

            // RecyclerView
//            adaptador = FilmsAdapter(filmList) { //TODO(canviar pulsar, mirar apunts listas final)
//                position -> verPeli(position)
//            }
//            val recyclerView: RecyclerView = findViewById(R.id.recyclerviewPelis)
//            recyclerView.layoutManager = LinearLayoutManager(this@FilmListActivity)
//
//            recyclerView.adapter = adaptador
        }
    }
    private fun initCompose() {
        setContent {
            MaterialTheme {
                ComposableFilmList()
            }
        }
    }
    @Composable
    private fun ComposableFilmList() {
        val context = LocalContext.current

        LazyColumn (
            modifier = Modifier
                .padding(15.dp, top = 50.dp)
        ){
            itemsIndexed(filmList){ index, peli ->
                FilmItem(
                    f = peli,
                    i = index
                )
            }
        }
    }
    @Composable
    fun FilmItem(f: Film, i: Int) {
        // == layout XML + bind ViewHolder
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { verPeli(i) }
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