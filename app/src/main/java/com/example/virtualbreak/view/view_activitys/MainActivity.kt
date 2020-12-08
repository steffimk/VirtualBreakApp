package com.example.virtualbreak.view.view_activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.virtualbreak.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var signupBtn: Button
    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signupBtn = findViewById(R.id.signupButton)
        signupBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        loginBtn = findViewById(R.id.loginButton)
        loginBtn.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

        go_to_groups_button.setOnClickListener{
            startActivity(Intent(this, GroupsActivity::class.java))
        }

    }
}