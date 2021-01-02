package com.example.virtualbreak.view.view_activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var signupBtn: Button
    lateinit var loginBtn: Button
    lateinit var test_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPrefManager.instance.init(applicationContext)

        if (Firebase.auth.currentUser != null) {
            // Save userId in shared preferences
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