package es.ua.eps.filmoteca

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class FilmsFragmentAdapter(
    context: Context, resource: Int,
    objects: List<Film>?
) : ArrayAdapter<Film>(context, resource, objects!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView?: LayoutInflater.from(this.context)
            .inflate(R.layout.item_peli, parent, false)

        val peliTitulo: TextView = view.findViewById(R.id.titulo)
        val peliDirector: TextView = view.findViewById(R.id.director)
        val peliImg: ImageView = view.findViewById(R.id.poster)

        getItem(position)?.let {
            peliTitulo.text = it.title
            peliDirector.text = it.director
            peliImg.setImageResource(it.imageResId)
        }

        return view
    }
}