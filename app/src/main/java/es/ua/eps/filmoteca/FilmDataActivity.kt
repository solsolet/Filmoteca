package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        const val EXTRA_FILM = "EXTRA_FILM" //ID
        const val EXTRA_FILM_TITLE = "EXTRA_FILM_TITLE"
    }
    //  Centralize Intents
    private fun verPeliIMDB(link: String?){
        val peliIMDB = Intent(Intent.ACTION_VIEW, link?.toUri())
        startActivity(peliIMDB)
    }
    val CODIGO_ACTIVIDAD_EDITAR = 1
    private fun editPeli(peli: Int){
        val edit = Intent(this@FilmDataActivity, FilmEditActivity::class.java)
        edit.putExtra(EXTRA_FILM, peli)
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
                    Mode.Bindings -> refreshBinding()
                    Mode.Compose -> refreshCompose()
                }
            }
        }
    }
    fun refreshBinding(){
        val peliInt = intent.getIntExtra(EXTRA_FILM, 0)
        val peli = FilmDataSource.films[peliInt]

        val generos = resources.getStringArray(R.array.generoPeli)
        val formatos = resources.getStringArray(R.array.formatoPeli)

        bindings.textViewTituloPeli.text = peli.title
        bindings.textViewDirectorPeli?.text = peli.director
        bindings.textViewAnyPeli.text = peli.year.toString()
        bindings.textViewNotas.text = peli.comments
        bindings.textViewGeneroPeli.text = "${generos[peli.genre]}"
        bindings.textViewFormatoPeli.text = "${formatos[peli.format]}"
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

            val peliInt = intent.getIntExtra(EXTRA_FILM, 0) //get ID
            val peli = FilmDataSource.films[peliInt]

            val generos = resources.getStringArray(R.array.generoPeli)
            val formatos = resources.getStringArray(R.array.formatoPeli)

            //Film data
            textViewTituloPeli.text = peli.title
            imageViewPeli.setImageResource(peli.imageResId)
            textViewDirectorPeli?.text = peli.director
            textViewAnyPeli.text = peli.year.toString()
            textViewGeneroPeli.text = "${generos[peli.genre]}"
            textViewFormatoPeli.text = "${formatos[peli.format]}"
            textViewNotas.append(": "+peli.comments)

            verPeliIMDB.setOnClickListener { verPeliIMDB(peli.imdbUrl) }
            editPeli.setOnClickListener { editPeli(peliInt) }
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

        Column( //= LinearLayout(vertical)
            modifier = Modifier
                .fillMaxSize()      // separated, class notes' style
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = peli + textEditado,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 50.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ){
                Image(
                    painter = painterResource(id = R.drawable.lalaland),
                    contentDescription = stringResource(R.string.contentImage),
                    modifier = Modifier
                        .width(165.dp)
                        .height(222.dp)
                )
                Column (
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                    horizontalAlignment = Alignment.Start
                ){
                    Text(stringResource(R.string.directorPeli)) //Director
                    Text(stringResource(R.string.anyPeli)) //Year
                    Text(stringResource(R.string.generoPeli)) //Genre
                    Text(stringResource(R.string.formatoPeli)) //Format
                }
            }
            Button(onClick = { //TODO: check Unit thing
                verPeliIMDB("http://www.imdb.com/title/tt0088763")
            }) {
                Text(stringResource(R.string.verIMDB))
            }

            Text(stringResource(R.string.notasPeli)) //Notes

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Button(onClick = {
                    editPeli(0) //TODO Ex5: Putting the right variable film from the getExtra
                }) {
                    Text(stringResource(R.string.editPeli))
                }

                Button(onClick = {
                    volverPrinc()
                }) {
                    Text(stringResource(R.string.volverPrincipal))
                }
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