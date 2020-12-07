package com.example.virtualbreak.view.view_activitys

//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualbreak.R
import com.example.virtualbreak.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso);

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        //TODO: updateUI(currentUser) --> currentUser is null if no sign in yet
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
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    // TODO: updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, R.string.toast_loginFailed,
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: updateUI(user)
                    // ...
                }
            }
    }

}