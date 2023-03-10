package com.example.virtualbreak.view.view_activitys

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * First view shown after app launch - shows welcome message and buttons to go to login or sign up
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var signupBtn: Button
    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPrefManager.instance.init(applicationContext)
        // If user is logged in, redirect to navigation drawer activity
        if (Firebase.auth.currentUser != null && Firebase.auth.currentUser!!.isEmailVerified) {
            // Save userId in shared preferences
            SharedPrefManager.instance.saveUserId(Firebase.auth.currentUser!!.uid)
            startActivity(Intent(this, NavigationDrawerActivity::class.java))
            return
        }

        setContentView(R.layout.activity_main)

        signupBtn = findViewById(R.id.signupButton)
        signupBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        loginBtn = findViewById(R.id.loginButton)
        loginBtn.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//disable rotate

    }

}