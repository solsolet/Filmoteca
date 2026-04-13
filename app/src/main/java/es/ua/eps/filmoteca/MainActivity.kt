package es.ua.eps.filmoteca

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), FilmListFragment.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (findViewById<View?>(R.id.isLargeLayout) != null) {
            // Ja hi ha fragments estàtics al XML, no fem res
            return
        }
        if (savedInstanceState == null) {
            val ppalFragment = FilmListFragment()
            ppalFragment.arguments = intent.extras

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ppalFragment)
                .commit()
        }
    }

    override fun onItemSelected(position: Int) {
        var detalleFragment = supportFragmentManager.findFragmentById(R.id.detalle_fragment) as FilmDataFragment?
        if (detalleFragment != null) {
            detalleFragment.setFilm(position)   // static: update fragment
        } else {
            detalleFragment = FilmDataFragment()// dinamic: transition to new fragment
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