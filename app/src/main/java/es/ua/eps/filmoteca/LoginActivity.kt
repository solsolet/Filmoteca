package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import es.ua.eps.filmoteca.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        private const val WEB_CLIENT_ID = "201223289832-7vic2uss7h3aj38rivn8qek1vujh9rhu.apps.googleusercontent.com"
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // If the user already signed in during a previous session, skip the login screen
        if (UserData.isSignedIn) {
            goToMainActivity()
            return
        }

        // Set up the sign-in button click listener
        binding.btnSignIn.setOnClickListener {
            launchGoogleSignIn()
        }
    }

    /**
     * Launches the Google Sign-In flow using the modern Credential Manager API.
     */
    private fun launchGoogleSignIn() {
        // 1. Configure WHAT we want to request from Google
        val googleIdOption = GetGoogleIdOption.Builder()
            // setFilterByAuthorizedAccounts(true) would only show accounts
            // that have already used this app before. We use false so ALL
            // Google accounts on the device are shown, even on first use.
            .setFilterByAuthorizedAccounts(false)
            // This tells Google which project/app is requesting sign-in.
            .setServerClientId(WEB_CLIENT_ID)
            // Requests a nonce for extra security (best practice).
            // In production you'd generate this server-side; here we skip it.
            .build()

        // 2. Build the credential request wrapping the Google option
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // 3. Get the CredentialManager system service
        val credentialManager = CredentialManager.create(this@LoginActivity)

        // 4. Launch the sign-in inside a coroutine
        lifecycleScope.launch {
            try {
                // This call shows the Google account picker dialog and
                // suspends (waits) until the user chooses an account or cancels.
                val result = credentialManager.getCredential( // is a SUSPEND function, meaning it must run inside a coroutine
                    request = request,
                    context = this@LoginActivity
                )

                // 5. Process the result
                handleSignInResult(result.credential)

            } catch (e: GetCredentialException) {
                // This catches cancellation, no accounts available, network error, etc.
                Log.e(TAG, "Sign-in failed: ${e.message}")
                Toast.makeText(
                    this@LoginActivity,
                    "Sign-in failed: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Processes the credential returned by the sign-in flow.
     * We extract the user's profile info from the Google ID token.
     */
    private fun handleSignInResult(credential: androidx.credentials.Credential) {
        // Check that we actually got a GoogleIdTokenCredential and not something else
        if (credential is androidx.credentials.CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

            // Parse the credential data into a structured GoogleIdTokenCredential object
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Store the user data in our singleton for use throughout the app
            UserData.idToken = googleIdTokenCredential.idToken
            UserData.displayName = googleIdTokenCredential.displayName
            UserData.email = googleIdTokenCredential.id
            UserData.photoUrl = googleIdTokenCredential.profilePictureUri?.toString()

            Log.d(TAG, "Signed in as: ${UserData.displayName} (${UserData.email})")

            // Navigate to the main app
            goToMainActivity()

        } else {
            // Got an unexpected credential type
            Log.e(TAG, "Unexpected credential type: ${credential.type}")
            Toast.makeText(this, "Unexpected sign-in error", Toast.LENGTH_SHORT).show()
        }
    }

    /** Navigates to MainActivity and removes LoginActivity from the back stack. */
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // FLAG_ACTIVITY_NEW_TASK + CLEAR_TASK ensures LoginActivity is removed from
        // the back stack so pressing Back from MainActivity won't return to login.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}