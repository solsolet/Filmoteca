package es.ua.eps.filmoteca

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), FilmListFragment.OnItemSelectedListener {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            // User denied — notifications won't show
            // In a real app you might show an explanation here
            Log.w("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.mtMainMenu))

        // This handles notch, status bar and navigation bar on all screen sizes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mtMainMenu)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        askNotificationPermission()

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

    /**
     * Requests POST_NOTIFICATIONS permission on Android 13+.
     * On older versions the permission is granted automatically, so we skip the request.
     */
    private fun askNotificationPermission() {
        // Only needed on Android 13 (API 33) and above
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        when {
            // Already granted — nothing to do
            ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("MainActivity", "Notification permission already granted")
            }

            // Should show rationale — user denied before, explain why we need it
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // For this practice a Toast is enough explanation
                // In a production app you'd show a proper dialog
                Toast.makeText(
                    this,
                    "Enable notifications to receive film updates",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            // First time asking — show the system dialog directly
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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