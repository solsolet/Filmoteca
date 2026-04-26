package es.ua.eps.filmoteca

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

/**
 * Receives geofence transition events from the OS.
 *
 * This is a BroadcastReceiver — it's woken up by the system when a geofence
 * transition occurs, even if the app is not running. It should do its work
 * quickly and not start long-running operations.
 *
 * The geofence ID we set was the film title, so we can look up which film
 * triggered this event and show useful information in the notification.
 */
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "GeofenceReceiver"
        private const val CHANNEL_ID = "geofence_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Parse the geofencing event from the received intent
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Geofencing error: ${geofencingEvent.errorCode}")
            return
        }

        val transition = geofencingEvent.geofenceTransition

        // We only care about ENTER and DWELL transitions
        if (transition != Geofence.GEOFENCE_TRANSITION_ENTER &&
            transition != Geofence.GEOFENCE_TRANSITION_DWELL) {
            Log.w(TAG, "Unhandled transition type: $transition")
            return
        }

        // Get all the geofences that fired in this event
        // (multiple geofences can fire at the same time if they overlap)
        val triggeringGeofences = geofencingEvent.triggeringGeofences ?: return

        // Build a notification for each triggered geofence
        for (geofence in triggeringGeofences) {
            val filmTitle = geofence.requestId // We set requestId = film title

            // Find the full film data to show richer info in the notification
            val film = FilmDataSource.films.find { it.title == filmTitle }

            val notificationTitle = "Near filming location!"
            val notificationText = if (film != null) {
                "You are near the filming location of \"${film.title}\" " +
                        "(${film.director ?: "Unknown"}, ${film.year})"
            } else {
                "You are near a filming location: $filmTitle"
            }

            showNotification(context, notificationTitle, notificationText)
            Log.d(TAG, "Geofence triggered for: $filmTitle")
        }
    }

    private fun showNotification(context: Context, title: String, text: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the channel (safe to call repeatedly, Android ignores duplicates)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Geofence Alerts",
            NotificationManager.IMPORTANCE_HIGH // HIGH so it appears as a heads-up notification
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text)) // expand for long text
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}