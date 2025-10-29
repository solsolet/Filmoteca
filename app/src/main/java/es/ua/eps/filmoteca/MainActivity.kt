package es.ua.eps.filmoteca

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import es.ua.eps.filmoteca.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FilmListFragment.OnItemSelectedListener {

    private lateinit var bindings : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setContentView(bindings.root)

        // Comprueba si estamos usando el layout dinámico
        if (findViewById<View?>(R.id.fragment_container) != null) {
            // Si se está restaurando, no hace falta cargar el fragmento
            if (savedInstanceState != null) return

            // Creamos el fragmento
            val ppalFragment = FilmListFragment()

            // Pasamos los extras del intent al fragmento
            ppalFragment.arguments = intent.extras

            // Añadimos el fragmento al contenedor
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ppalFragment).commit()
        }
    }

    override fun onItemSelected(position: Int) {
        var detalleFragment = supportFragmentManager.findFragmentById(R.id.detalle_fragment) as FilmDataFragment?
        if (detalleFragment != null) {
            // Tipo estático: actualizamos directamente el fragmento
            detalleFragment.setFilm(position)
        } else {
            // Tipo dinámico: hacemos transición al nuevo fragmento
            detalleFragment = FilmDataFragment()
            val args = Bundle()
            args.putInt(FilmDataFragment.Companion.EXTRA_FILM_INDEX, position)
            detalleFragment.arguments = args
            val t = supportFragmentManager.beginTransaction()
            t.replace(R.id.fragment_container, detalleFragment)
            t.addToBackStack(null)
            t.commit()
        }
    }
}