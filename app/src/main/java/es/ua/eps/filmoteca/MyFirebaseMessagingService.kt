package es.ua.eps.filmoteca

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * This service runs in the background and is called by Firebase whenever
 * a message is delivered to this device.
 *
 * IMPORTANT: It only runs when the message is a DATA MESSAGE (not a notification message).
 * Data messages always wake this service. Notification messages only wake it in background.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM"
        private const val CHANNEL_ID = "filmoteca_channel"

        // Keys expected in the FCM data payload
        private const val KEY_OPERATION = "operation"   // "add" or "delete"
        private const val KEY_TITLE = "title"
        private const val KEY_DIRECTOR = "director"
        private const val KEY_YEAR = "year"
        private const val KEY_COMMENTS = "comments"

        // Operation values
        private const val OP_ADD = "add"
        private const val OP_DELETE = "delete"
    }

    /**
     * Called when a new FCM registration token is generated.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        // TODO: send this token to your server if you have one
    }

    /**
     * Called when a data message is received.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Message received from: ${message.from}")
        Log.d(TAG, "Data payload: ${message.data}")

        // Only process data messages (not notification messages)
        if (message.data.isEmpty()) {
            Log.d(TAG, "Received notification message (no data), ignoring")
            return
        }

        // Extract the operation type from the payload
        val operation = message.data[KEY_OPERATION]

        when (operation) {
            OP_ADD -> handleAddOrUpdate(message.data)
            OP_DELETE -> handleDelete(message.data)
            else -> Log.w(TAG, "Unknown operation: $operation")
        }
    }

    /**
     * Handles the "add" operation.
     * Business rule: if a film with the same title already exists → UPDATE it.
     *                if it does not exist → ADD it as new.
     */
    private fun handleAddOrUpdate(data: Map<String, String>) {
        val title = data[KEY_TITLE] ?: run {
            Log.w(TAG, "Add operation received without a title, ignoring")
            return
        }

        // Build a Film object from the received data
        val newFilm = Film().apply {
            this.title = title
            this.director = data[KEY_DIRECTOR] ?: ""
            this.year = data[KEY_YEAR]?.toIntOrNull() ?: 0
            this.comments = data[KEY_COMMENTS] ?: ""
            this.imageResId = R.mipmap.ic_launcher // default image
        }

        // Check if a film with the same title already exists
        val existingIndex = FilmDataSource.films.indexOfFirst { it.title == title }

        if (existingIndex >= 0) {
            // Film exists → UPDATE it in place
            FilmDataSource.films[existingIndex] = newFilm
            Log.d(TAG, "Updated existing film: $title")
            showNotification("Film Updated", title)
        } else {
            // Film does not exist → ADD it
            FilmDataSource.films.add(newFilm)
            Log.d(TAG, "Added new film: $title")
            showNotification("New Film", title)
        }
    }

    /**
     * Handles the "delete" operation.
     * Business rule: if the film exists → REMOVE it.
     *                if it does not exist → do nothing.
     */
    private fun handleDelete(data: Map<String, String>) {
        val title = data[KEY_TITLE] ?: run {
            Log.w(TAG, "Delete operation received without a title, ignoring")
            return
        }

        val removed = FilmDataSource.films.removeAll { it.title == title }

        if (removed) {
            Log.d(TAG, "Deleted film: $title")
            showNotification("Film Deleted", title)
        } else {
            Log.d(TAG, "Film not found for deletion: $title")
        }
    }

    /**
     * Shows a system notification to the user.
     */
    private fun showNotification(title: String, filmTitle: String) {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel (safe to call multiple times, Android ignores duplicates)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Filmoteca Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        // Build the notification
        val notification = NotificationCompat.Builder(this@MyFirebaseMessagingService, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(filmTitle)
            .setAutoCancel(true)
            .build()

        // Use a timestamp-based ID so multiple notifications don't overwrite each other
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}