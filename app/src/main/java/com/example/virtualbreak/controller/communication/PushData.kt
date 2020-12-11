package com.example.virtualbreak.controller.communication

import android.util.Log
import com.example.virtualbreak.model.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class PushData {

    companion object {

        private val database : DatabaseReference = Firebase.database.reference
        private const val TAG: String = "PushData"

        fun saveUser(user: FirebaseUser, name: String) {
            if (user != null) {
                  val userData = User(user.uid, name, Status.BUSY)
                  database.child("users").child(user.uid).setValue(userData)
            } else {
                Log.d(TAG, "No user logged in. Cannot save user.")
            }
        }

        fun saveGroup(description: String) : String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val groupId = database.child("groups").push().key
                if (groupId != null) {
                    val newGroup = Group(groupId, hashMapOf(UUID.randomUUID().toString() to currentUserId), description)
                    database.child("groups").child(groupId).setValue(newGroup)
                    database.child("users").child(currentUserId).child("groups").push().setValue(groupId)
                }
                return groupId
            } else {
                Log.d(TAG, "No user logged in. Cannot save group.")
                return null
            }
        }

        fun joinGroup(groupId: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child("groups").child(groupId).child("users").push().setValue(currentUserId)
                database.child("users").child(currentUserId).child("groups").push().setValue(groupId)
            } else {
                Log.d(TAG, "No user logged in. Cannot join group.")
            }
        }

        fun saveRoom(groupId: String, roomType: Roomtype?) : String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val roomId = database.child("rooms").push().key
                if (roomId != null) {
                    val newRoom = Room(roomId, hashMapOf(UUID.randomUUID().toString() to currentUserId), roomType?: Roomtype.COFFEE)
                    database.child("rooms").child(roomId).setValue(newRoom)
                    database.child("groups").child(groupId).child("rooms").push().setValue(roomId)
                }
                return roomId
            } else {
                Log.d(TAG, "No user logged in. Cannot save room.")
                return null
            }
        }

        fun joinRoom(roomId: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child("rooms").child(roomId).child("users").push().setValue(currentUserId)
            } else {
                Log.d(TAG, "No user logged in. Cannot join room.")
            }
        }

        fun setStatus(status: Status) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child("users").child(currentUserId).child("status").setValue(status)
            } else {
                Log.d(TAG, "No user logged in. Cannot change status.")
            }
        }

        fun setProfilPicture(picture: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child("users").child(currentUserId).child("profilePicture").setValue(picture)
            } else {
                Log.d(TAG, "No user logged in. Cannot save profile picture.")
            }
        }

        fun addFriends(vararg friendId: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                for (id in friendId) {
                    database.child("users").child(currentUserId).child("friends").push().setValue(friendId)
                }
            } else {
                Log.d(TAG, "No user logged in. Cannot save add friends.")
            }
        }

        fun setGroupDescription(groupId: String, description: String) {
            database.child("groups").child(groupId).child("description").setValue(description)
        }
    }
}