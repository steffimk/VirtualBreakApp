package com.example.virtualbreak.view.view_activitys

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var signupBtn: Button
    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPrefManager.instance.init(applicationContext)

        // If user is logged in, redirect to navigation drawer activity
        if (Firebase.auth.currentUser != null) {
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

    /*override fun onBackPressed() { //not needed, because redirect NavigationDrawerActivity when user not logged in
        // Do nothing, to prevent going back to NavigationDrawerActivity after Logout
    }*/

}