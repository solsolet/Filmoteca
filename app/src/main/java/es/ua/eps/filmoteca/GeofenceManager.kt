package es.ua.eps.filmoteca

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

/**
 * Singleton that manages geofence registration and removal.
 *
 * A geofence is a virtual perimeter around a real-world location.
 * When the device enters (or is already inside) that perimeter,
 * the system fires a broadcast that our GeofenceBroadcastReceiver handles.
 *
 * Key concept: geofences are registered with the OS-level LocationServices,
 * NOT stored in memory. This means they survive app restarts. The only thing
 * we need to persist ourselves is the hasGeofence flag on each Film.
 */
object GeofenceManager {

    private const val TAG = "GeofenceManager"

    // The geofence settings defined by the practice requirements
    private const val GEOFENCE_RADIUS_METERS = 500f
    private const val GEOFENCE_EXPIRATION = Geofence.NEVER_EXPIRE

    /**
     * The PendingIntent that wraps our BroadcastReceiver.
     * The OS holds onto this and fires it when a geofence transition occurs.
     * We use FLAG_UPDATE_CURRENT so that if we call addGeofences again,
     * the same PendingIntent is reused (not duplicated).
     * We use FLAG_MUTABLE because the system needs to fill in the Intent extras.
     */
    private fun getGeofencePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    /**
     * Registers a geofence for a film.
     * Uses the film's title as the geofence ID — this lets us identify
     * which film triggered the geofence in the BroadcastReceiver.
     *
     * @param context   Application context
     * @param film      The film whose location to geofence
     * @param onSuccess Called when the geofence is successfully registered
     * @param onFailure Called with an error message if registration fails
     */
    fun addGeofence(
        context: Context,
        film: Film,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (!film.hasLocation) {
            onFailure("This film has no location set. Set coordinates first.")
            return
        }

        val geofence = Geofence.Builder()
            // Use the film title as a unique ID for this geofence
            .setRequestId(film.title ?: "unknown")
            // Define the circular region: center + radius
            .setCircularRegion(film.latitude, film.longitude, GEOFENCE_RADIUS_METERS)
            // Never expire — stays until the user removes it or the app uninstalls
            .setExpirationDuration(GEOFENCE_EXPIRATION)
            // Fire when entering the geofence, AND if we're already inside when registering
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_DWELL
            )
            // DWELL requires a loitering delay — how long inside before firing DWELL
            .setLoiteringDelay(30_000) // 30 seconds
            .build()

        val request = GeofencingRequest.Builder()
            // INITIAL_TRIGGER_ENTER: also fire if already inside the fence when registering
            .setInitialTrigger(
                GeofencingRequest.INITIAL_TRIGGER_ENTER or
                        GeofencingRequest.INITIAL_TRIGGER_DWELL
            )
            .addGeofence(geofence)
            .build()

        val client = LocationServices.getGeofencingClient(context)

        // addGeofences requires ACCESS_FINE_LOCATION permission.
        // We check this before calling addGeofence() in the UI layer,
        // but the try/catch here is a safety net.
        try {
            client.addGeofences(request, getGeofencePendingIntent(context))
                .addOnSuccessListener {
                    Log.d(TAG, "Geofence added for: ${film.title}")
                    film.hasGeofence = true
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to add geofence: ${e.message}")
                    onFailure(e.message ?: "Unknown error")
                }
        } catch (e: SecurityException) {
            onFailure("Location permission not granted: ${e.message}")
        }
    }

    /**
     * Removes the geofence for a film.
     * We remove by request ID (the film title).
     */
    fun removeGeofence(
        context: Context,
        film: Film,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val client = LocationServices.getGeofencingClient(context)
        client.removeGeofences(listOf(film.title ?: "unknown"))
            .addOnSuccessListener {
                Log.d(TAG, "Geofence removed for: ${film.title}")
                film.hasGeofence = false
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to remove geofence: ${e.message}")
                onFailure(e.message ?: "Unknown error")
            }
    }
}