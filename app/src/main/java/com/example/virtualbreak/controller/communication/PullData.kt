package com.example.virtualbreak.controller.communication

import android.util.Log
import com.example.virtualbreak.model.Group
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PullData {

    companion object {

        private val database : DatabaseReference = Firebase.database.reference
        private const val TAG: String = "PullData"
        var currentUser: User? = null

        fun attachListenerToCurrentUser() {
            val valueEventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    currentUser = dataSnapshot.getValue(User::class.java)
                    Log.d(TAG, "Pulled User: $currentUser");
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.message)
                }
            }

            val userUid = Firebase.auth.currentUser?.uid
            if (userUid != null) {
                database.child("users").child(userUid).addValueEventListener(valueEventListener)
            }
        }

        fun attachListenerToGroup(groupId: String) {
            val valueEventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val group = dataSnapshot.getValue(Group::class.java)
                    Log.d(TAG, "Pulled Group: $group");
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.message)
                }
            }
            database.child("groups").child(groupId).addValueEventListener(valueEventListener)
        }

        fun attachListenerToRoom(roomId: String) {
            val valueEventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val room = dataSnapshot.getValue(Room::class.java)
                    Log.d(TAG, "Pulled Room: $room");
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.message)
                }
            }
            database.child("rooms").child(roomId).addValueEventListener(valueEventListener)
        }
    }
}


