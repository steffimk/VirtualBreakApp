package com.example.virtualbreak.controller.communication

import com.example.virtualbreak.model.Status
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PushData {

    companion object {

        fun saveUser(user: FirebaseUser, name: String) {

            var database: DatabaseReference = Firebase.database.reference
            if (user != null) {
                database.child("users").child(user.uid).child("username").setValue(name)
                database.child("users").child(user.uid).child("status").setValue(Status.BUSY.dbStr)
            }

        }
    }
}