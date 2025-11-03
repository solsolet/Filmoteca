package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.net.toUri

class FilmDataFragment : Fragment() {

    var filmIndex = -1
    companion object {
        const val EXTRA_FILM_INDEX = "EXTRA_FILM_INDEX"
        private const val REQUEST_CODE_EDIT = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_film_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filmIndex = arguments?.getInt(EXTRA_FILM_INDEX, -1) ?: -1

        val btnImdb = view.findViewById<Button>(R.id.verPeliIMDB)
        val btnEditar = view.findViewById<Button>(R.id.editPeli)
        val btnVolver = view.findViewById<Button>(R.id.volverPrincipal)

        btnImdb.setOnClickListener {
            if (filmIndex >= 0) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = FilmDataSource.films[filmIndex].imdbUrl?.toUri()
                startActivity(intent)
            }
        }

        btnEditar.setOnClickListener {
            val intent = Intent(requireActivity(), FilmEditActivity::class.java)
            intent.putExtra(EXTRA_FILM_INDEX, filmIndex)
            startActivityForResult(intent, REQUEST_CODE_EDIT)
        }

        btnVolver.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        updateFilmData(view)
    }

    private fun updateFilmData(view : View) {
        if (filmIndex < 0) return

        val txtTitulo = view.findViewById<TextView>(R.id.textViewTituloPeli)
        val txtAnyo = view.findViewById<TextView>(R.id.textViewAnyPeli)
        val txtDirector = view.findViewById<TextView>(R.id.textViewDirectorPeli)
        val txtComentarios = view.findViewById<TextView>(R.id.textViewNotas)
        val txtGenero = view.findViewById<TextView>(R.id.textViewGeneroPeli)
        val txtFormato = view.findViewById<TextView>(R.id.textViewFormatoPeli)
        val imgPoster = view.findViewById<ImageView>(R.id.imageViewPeli)

        val film = FilmDataSource.films[filmIndex]
        txtTitulo.text = film.title
        txtDirector.text = film.director
        txtAnyo.text = film.year.toString()
        txtComentarios.text = film.comments
        txtGenero.text = resources.getStringArray(R.array.generoPeli)[film.genre]
        txtFormato.text = resources.getStringArray(R.array.formatoPeli)[film.format]
        imgPoster.setImageResource(film.imageResId)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT && resultCode == Activity.RESULT_OK) {
            view?.let { updateFilmData(it) }
        }
    }

    fun setFilm(position: Int) {
        filmIndex = position
        view?.let { updateFilmData(it) }
    }
}