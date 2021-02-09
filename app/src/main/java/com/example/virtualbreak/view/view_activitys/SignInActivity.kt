package com.example.virtualbreak.view.view_activitys

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.virtualbreak.controller.isOnline
import com.google.firebase.auth.FirebaseUser

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding

    private val TAG: String = "SignInActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signinSubmit.setOnClickListener {
            tryAndSignUp(binding.singinName.text.toString(), binding.signinEmail.text.toString(),
                    binding.signinPassword1.text.toString(), binding.signinPassword2.text.toString())
        }

        binding.signUpLogin.setOnClickListener {
            //Navigate to LogInActivity
            startActivity(Intent(this, LogInActivity::class.java))
        }

        auth = Firebase.auth

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//disable rotate
    }

    override fun onStart() {
        super.onStart()
        if ( auth.currentUser != null && auth.currentUser!!.isEmailVerified)
            startActivity(Intent(this, NavigationDrawerActivity::class.java))
    }

    /**
     * Check whether info was entered correctly, register a new user in firebase and send a verification mail
     */
    private fun tryAndSignUp(name: String, email: String, password1: String, password2: String) {
        if (name.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            Toast.makeText(
                    baseContext, R.string.toast_enter_data,
                    Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (password1 != password2){
            Toast.makeText(
                    baseContext, R.string.toast_pw_different,
                    Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (isOnline(this.applicationContext)) {
            auth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            if (user != null) {
                                sendVerificationMail(user,name)
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            val message: String = getGermanErrorMessage((task.exception as FirebaseAuthException?)!!.errorCode, R.string.toast_signIn_Failed.toString())
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

    /**
     * Try to send a verification mail to the new user. Delete user if not successful.
     */
    private fun sendVerificationMail(user: FirebaseUser, userName: String){
        auth.useAppLanguage()
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Verification email sent.")
                    PushData.saveUser(user, userName)
                    Toast.makeText(
                        baseContext, getString(R.string.verification_mail_sent),
                        Toast.LENGTH_SHORT
                    ).show()
//                    auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Log.d(TAG, "Could not sent verification email.")
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) Log.d(TAG, "User account deleted.")
                        }
                    Toast.makeText(
                        baseContext, getString(R.string.verification_mail_failure),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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