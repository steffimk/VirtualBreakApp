package com.example.virtualbreak.controller.communication

import com.example.virtualbreak.model.Status
import com.example.virtualbreak.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PushData {

    companion object {

        private val database : DatabaseReference = Firebase.database.reference

        fun saveUser(user: FirebaseUser, name: String) {

            val userData = User(name, Status.BUSY)
            if (user != null) {
                  database.child("users").child(user.uid).setValue(userData)
            }

        }
    }
}