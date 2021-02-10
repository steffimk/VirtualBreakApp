package com.example.virtualbreak.controller.communication

import android.util.Log
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.Status
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

/**
 * Pulls data from realtime database independent of any view
 */
class PullData {

    companion object {

        private const val TAG: String = "PullData"

        val database: DatabaseReference = Firebase.database.reference

        var currentStatus: Status? = null
        var currentRoom: Room? = null

        /**
         * Pulls own user name once from database and saves it in shared preferences
         */
        fun pullAndSaveOwnUserName() {

            val userNameListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val pulledName = dataSnapshot.getValue<String>()
                    Log.d(TAG, "Pulled username $pulledName")
                    if (pulledName == null) return
                    else SharedPrefManager.instance.saveUserName(pulledName)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, error.message)
                }

            }

            database.child(Constants.DATABASE_CHILD_USERS)
                .child(SharedPrefManager.instance.getUserId() ?: "")
                .child(Constants.DATABASE_CHILD_USERNAME)
                .addListenerForSingleValueEvent(userNameListener)
        }

        /**
         * Attaches permanent event listener to status of own user and saves it in currentStatus
         */
        fun attachListenerToStatus() {
            if (currentStatus != null) {
                return // Listener already attached
            }

            val valueEventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    currentStatus = dataSnapshot.getValue(Status::class.java)
                    Log.d(TAG, "Pulled Status: " + currentStatus)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.message)
                }
            }

            val userUid = Firebase.auth.currentUser?.uid
            if (userUid != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(userUid)
                    .child(Constants.DATABASE_CHILD_STATUS).addValueEventListener(valueEventListener)
            }
        }

    }
}


