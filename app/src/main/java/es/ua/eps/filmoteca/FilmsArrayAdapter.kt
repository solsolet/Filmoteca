package es.ua.eps.filmoteca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView


class FilmsArrayAdapter(
    context: FilmListActivity, resource: Int,
    objects: List<Film>?
) : ArrayAdapter<Film>(context, resource, objects!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View = convertView?: LayoutInflater.from(this.context)
            .inflate(R.layout.item_peli, parent, false)

        val peliTitulo   = view.findViewById(R.id.titulo) as TextView
        val peliDirector = view.findViewById(R.id.director) as TextView
        val peliImg      = view.findViewById(R.id.poster) as ImageView

        getItem(position)?.let {
            peliTitulo.text = it.title
            peliDirector.text = it.director
            peliImg.setImageResource(it.imageResId)
        }

        return view
    }
}