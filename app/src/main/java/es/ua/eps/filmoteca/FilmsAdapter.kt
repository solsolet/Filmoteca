package es.ua.eps.filmoteca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilmsAdapter(
    private val pelis: List<Film>,
    private val onPeliClick: (position: Int) -> Unit
) : RecyclerView.Adapter<FilmsAdapter.ViewHolder?>() {

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_peli, parent, false)
        return ViewHolder(v, onPeliClick)
    }

    override fun onBindViewHolder( holder: ViewHolder, position: Int ) {
        holder.bind(pelis[position])
    }

    override fun getItemCount(): Int {
        return pelis.size
    }

    class ViewHolder(
        v: View,
        private val onPeliClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(v){
        private var titulo: TextView = v.findViewById(R.id.titulo)
        private var direc: TextView = v.findViewById(R.id.director)
        private var poster: ImageView = v.findViewById(R.id.poster)

        init {
            v.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    onPeliClick(position)
                }
            }
        }

        fun bind(f: Film) {
            titulo.text = f.title
            direc.text = f.director
            poster.setImageResource(f.imageResId)
        }
    }
}