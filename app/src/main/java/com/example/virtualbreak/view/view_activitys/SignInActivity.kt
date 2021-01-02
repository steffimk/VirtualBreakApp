package com.example.virtualbreak.view.view_activitys

import android.content.Intent
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.virtualbreak.controller.isOnline

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
    }

    override fun onStart() {
        super.onStart()
        if ( auth.currentUser != null)
            startActivity(Intent(this, NavigationDrawerActivity::class.java))
    }

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
                                // Save userId in shared preferences
                                SharedPrefManager.instance.saveUserId(user.uid)
                                PushData.saveUser(user, name)
                            }
                            startActivity(Intent(this, NavigationDrawerActivity::class.java))
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


}