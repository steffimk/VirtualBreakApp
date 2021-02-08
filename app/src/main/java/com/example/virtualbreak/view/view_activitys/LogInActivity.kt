package com.example.virtualbreak.view.view_activitys

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.virtualbreak.controller.isOnline
import com.example.virtualbreak.model.Status
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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

        binding.resetPassword.visibility = View.GONE

        auth = Firebase.auth

        binding.resetPassword.setOnClickListener {
            this.auth.useAppLanguage()
            Firebase.auth.sendPasswordResetEmail(binding.loginEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Sent mail to reset password.")
                        Toast.makeText(
                            baseContext, R.string.toast_sent_reset_mail,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.resetPassword.visibility = View.GONE
                    }
                }
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//disable rotate
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            SharedPrefManager.instance.saveUserId(auth.currentUser!!.uid)
            Log.d(TAG, "User angemeldet uid: " + auth.currentUser!!.uid)
            startActivity(Intent(this, NavigationDrawerActivity::class.java))
        }
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
                            PushData.setStatus(Status.AVAILABLE)
                            startActivity(Intent(this, NavigationDrawerActivity::class.java))
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            val errorCode = (task.exception as FirebaseAuthException?)!!.errorCode
                            val message: String = getGermanErrorMessage(errorCode, R.string.toast_loginFailed.toString())
                            Toast.makeText(
                                    baseContext, message,
                                    Toast.LENGTH_SHORT
                            ).show()
                            if (errorCode === "ERROR_WRONG_PASSWORD"){
                                binding.resetPassword.visibility = View.VISIBLE
                            }
                        }
                    }
        } else {
            Toast.makeText(
                    baseContext, R.string.internet_access_required,
                    Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun getGermanErrorMessage(errorCode: String, defaultMessage: String): String {
        return when(errorCode) {
            "ERROR_INVALID_CUSTOM_TOKEN" -> getString(R.string.ERROR_INVALID_CUSTOM_TOKEN)
            "ERROR_CUSTOM_TOKEN_MISMATCH"-> getString(R.string.ERROR_CUSTOM_TOKEN_MISMATCH)
            "ERROR_INVALID_CREDENTIAL" -> getString(R.string.ERROR_INVALID_CREDENTIAL)
            "ERROR_INVALID_EMAIL" -> getString(R.string.ERROR_INVALID_EMAIL)
            "ERROR_WRONG_PASSWORD" -> getString(R.string.ERROR_WRONG_PASSWORD)
            "ERROR_USER_MISMATCH" -> getString(R.string.ERROR_USER_MISMATCH)
            "ERROR_REQUIRES_RECENT_LOGIN" -> getString(R.string.ERROR_REQUIRES_RECENT_LOGIN)
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> getString(R.string.ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL)
            "ERROR_EMAIL_ALREADY_IN_USE" -> getString(R.string.ERROR_EMAIL_ALREADY_IN_USE)
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> getString(R.string.ERROR_CREDENTIAL_ALREADY_IN_USE)
            "ERROR_USER_DISABLED" -> getString(R.string.ERROR_USER_DISABLED)
            "ERROR_USER_TOKEN_EXPIRED" -> getString(R.string.ERROR_USER_TOKEN_EXPIRED)
            "ERROR_USER_NOT_FOUND" -> getString(R.string.ERROR_USER_NOT_FOUND)
            "ERROR_INVALID_USER_TOKEN" -> getString(R.string.ERROR_INVALID_USER_TOKEN)
            "ERROR_OPERATION_NOT_ALLOWED" -> getString(R.string.ERROR_OPERATION_NOT_ALLOWED)
            "ERROR_WEAK_PASSWORD" -> getString(R.string.ERROR_WEAK_PASSWORD)
            "ERROR_MISSING_EMAIL" -> getString(R.string.ERROR_MISSING_EMAIL)
            else -> defaultMessage
        }
    }
}

