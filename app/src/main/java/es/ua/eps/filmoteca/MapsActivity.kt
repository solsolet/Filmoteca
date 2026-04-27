package es.ua.eps.filmoteca

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Activity that shows a Google Map centered on the filming location of a film.
 *
 * It implements OnMapReadyCallback — this is the pattern required by the Maps SDK.
 * We ask the SupportMapFragment for the map asynchronously (getMapAsync),
 * and when it's ready the SDK calls our onMapReady() method.
 * We never try to use the map before onMapReady fires.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var film: Film

    companion object {
        const val EXTRA_FILM_INDEX = "film_index"
        private const val DEFAULT_ZOOM = 12f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Retrieve the film using the index passed from FilmDataActivity
        val filmIndex = intent.getIntExtra(EXTRA_FILM_INDEX, -1)
        if (filmIndex < 0 || filmIndex >= FilmDataSource.films.size) {
            // Safety check — if something went wrong, just close
            finish()
            return
        }
        film = FilmDataSource.films[filmIndex]

        // Get the SupportMapFragment declared in the layout and register
        // our callback. The map will call onMapReady() when it's fully loaded.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Called by the Maps SDK when the GoogleMap object is ready to use.
     * All map setup MUST happen here or after this call — never before.
     */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val filmLocation = LatLng(film.latitude, film.longitude)

        // addMarker returns a Marker? — save it so we can call showInfoWindow()
        // Add a marker at the filming location.
        // .title()   → shown as the bold header in the info window
        // .snippet() → shown as the smaller text below the title
        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(filmLocation)
                .title(film.title ?: "Unknown film")
                .snippet("${film.director ?: "Unknown director"} · ${film.year}")
        )

        // Move the camera to the marker and set a comfortable zoom level.
        // moveCamera() is instant; use animateCamera() if you want a smooth animation.
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(filmLocation, DEFAULT_ZOOM)
        )

        // Open the info window automatically
        marker?.showInfoWindow()
    }
}