package com.example.virtualbreak.view.view_activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var signupBtn: Button
    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPrefManager.instance.init(applicationContext)

        if (Firebase.auth.currentUser != null) {
            PullData.attachListenerToCurrentUser()
            SharedPrefManager.instance.saveUserId(Firebase.auth.currentUser!!.uid)
            startActivity(Intent(this, NavigationDrawerActivity::class.java))
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

    }
}