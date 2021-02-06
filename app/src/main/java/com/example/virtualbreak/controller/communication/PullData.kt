package com.example.virtualbreak.controller.communication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class PullData {

    companion object {

        private const val TAG: String = "PullData"

        val database: DatabaseReference = Firebase.database.reference

        var currentUser: MutableLiveData<User?> = MutableLiveData(null)

        var currentRoom: Room? = null

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

        fun attachListenerToCurrentUser() {
            if (currentUser.value != null) {
                return // Listener already attached
            }

            val valueEventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    currentUser.value = dataSnapshot.getValue(User::class.java)
                    Log.d(TAG, "Pulled User: " + currentUser.value)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.message)
                }
            }

            val userUid = Firebase.auth.currentUser?.uid
            if (userUid != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(userUid)
                    .addValueEventListener(valueEventListener)
            }
        }

        fun pullcurrentRoom() {

            database.child(Constants.DATABASE_CHILD_ROOMS)
                .child(SharedPrefManager.instance.getRoomId() ?: "")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val pulledRoom = dataSnapshot.getValue<Room>()
                        Log.d(TAG, "Pulled username $pulledRoom")
                        Log.d("CHECK", "Pulled currentRoom $pulledRoom")
                        // if (pulledRoom == null) return
                        currentRoom = pulledRoom
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG, error.message)
                    }
                })
        }

    }
}


