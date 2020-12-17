package com.example.virtualbreak.view.view_activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.virtualbreak.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var signupBtn: Button
    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Firebase.auth.currentUser != null){

            // TODO: Der User ist angemeldet. Hier Startbildschirm der App nach Authentication laden
            /*setContentView(R.layout.activity_main)

            signupBtn = findViewById(R.id.signupButton)
            signupBtn.text = "Sign Out"
            signupBtn.setOnClickListener{
                Firebase.auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
            }

            loginBtn = findViewById(R.id.loginButton)
            loginBtn.visibility = View.INVISIBLE*/
            startActivity(Intent(this, NavigationDrawerActivity::class.java))
        }

        else {
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
}