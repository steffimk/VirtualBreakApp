package com.example.virtualbreak.view.view_activitys

//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.virtualbreak.controller.isOnline

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    //    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityLogInBinding

    private val TAG: String = "LogInActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginSubmit.setOnClickListener {
            tryAndLogIn(binding.loginEmail.text.toString(), binding.loginPassword.text.toString())
        }

        binding.loginSignUp.setOnClickListener {
            //navigate to SignInActivity
            startActivity(Intent(this, SignInActivity::class.java))
        }
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso);

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null)
            SharedPrefManager.instance.saveUserId(auth.currentUser!!.uid)
            startActivity(Intent(this, NavigationDrawerActivity::class.java))
    }

    private fun tryAndLogIn(email: String, password: String) {
        if (email.isEmpty()) {
            Toast.makeText(
                    baseContext, R.string.toast_enter_mail,
                    Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(
                    baseContext, R.string.toast_enter_pw,
                    Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (isOnline(this.applicationContext)) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            // Save userId in shared preferences
                            SharedPrefManager.instance.saveUserId(auth.currentUser!!.uid)
                            startActivity(Intent(this, NavigationDrawerActivity::class.java))
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            val message: String = getGermanErrorMessage((task.exception as FirebaseAuthException?)!!.errorCode, R.string.toast_loginFailed.toString())
                            Toast.makeText(
                                    baseContext, message,
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
        } else {
            Toast.makeText(
                    baseContext, R.string.internet_access_required,
                    Toast.LENGTH_SHORT
            ).show()
        }

    }
}

fun getGermanErrorMessage(errorCode: String, defaultMessage: String): String {
    return when(errorCode) {
        "ERROR_INVALID_CUSTOM_TOKEN" -> "Das benutzerdefinierte Token-Format ist falsch. Bitte prüfen Sie die Dokumentation."
        "ERROR_CUSTOM_TOKEN_MISMATCH"-> "Das benutzerdefinierte Token korrespondiert mit einem anderen Adressaten."
        "ERROR_INVALID_CREDENTIAL" -> "Der gelieferte Berechtigungsnachweis ist falsch formatiert oder abgelaufen."
        "ERROR_INVALID_EMAIL" -> "Die Mailadresse ist falsch formatiert."
        "ERROR_WRONG_PASSWORD" -> "Ungültiges Passwort."
        "ERROR_USER_MISMATCH" -> "Die angegebenen Anmeldedaten stimmen nicht mit dem zuvor angemeldeten Benutzer überein."
        "ERROR_REQUIRES_RECENT_LOGIN" -> "Dieser Vorgang ist sensibel und erfordert eine aktuelle Authentifizierung. Melden Sie sich erneut an, bevor Sie diese Anfrage versuchen."
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "Es existiert bereits ein Konto mit dieser Mailadresse"
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Es existiert bereits ein Konto mit dieser Mailadresse."
        "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "Dieser Zugangscode ist bereits mit einem anderen Benutzerkonto verknüpft."
        "ERROR_USER_DISABLED" -> "Das Benutzerkonto wurde deaktiviert."
        "ERROR_USER_TOKEN_EXPIRED" -> "Der Benutzer muss sich erneut anmelden."
        "ERROR_USER_NOT_FOUND" -> "Benutzer nicht gefunden. Überprüfe die Mailadresse."
        "ERROR_INVALID_USER_TOKEN" -> "Der Benutzer muss sich erneut anmelden."
        "ERROR_OPERATION_NOT_ALLOWED" -> "Dieser Vorgang ist nicht erlaubt."
        "ERROR_WEAK_PASSWORD" -> "Das Passwort ist zu kurz."
        "ERROR_MISSING_EMAIL" -> "Mailadresse muss angegeben werden."
        else -> defaultMessage
    }
}