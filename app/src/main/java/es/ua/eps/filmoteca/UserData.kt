package es.ua.eps.filmoteca

// Singleton object to hold user session data across the whole app
object UserData {
    var displayName: String? = null
    var email: String? = null
    var photoUrl: String? = null
    var idToken: String? = null

    // Returns true if a user is currently signed in
    val isSignedIn: Boolean
        get() = idToken != null

    // Clears all user data on sign out
    fun clear() {
        displayName = null
        email = null
        photoUrl = null
        idToken = null
    }
}