package com.example.virtualbreak.controller.communication

import android.util.Log
import com.example.virtualbreak.model.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.HashMap

class PushData {

    companion object {

        private val database : DatabaseReference = Firebase.database.reference
        private const val TAG: String = "PushData"

        fun saveUser(user: FirebaseUser, name: String) {
            if (user?.email != null) {
                  val userData = User(user.uid, name, user.email!!, Status.BUSY)
                  database.child("users").child(user.uid).setValue(userData)
                Log.d(TAG, "Saved user to realtime database")
            } else {
                Log.d(TAG, "No user logged in. Cannot save user.")
            }
        }

        fun saveGroup(description: String) : String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val groupId = database.child("groups").push().key
                if (groupId != null) {
                    val newGroup = Group(groupId, hashMapOf(currentUserId to currentUserId), description)
                    database.child("groups").child(groupId).setValue(newGroup)
                    database.child("users").child(currentUserId).child("groups").child(groupId).setValue(groupId)
                    Log.d(TAG, "Saved new group")
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
                database.child("groups").child(groupId).child("users").child(currentUserId).setValue(currentUserId)
                database.child("users").child(currentUserId).child("groups").child(groupId).setValue(groupId)
                Log.d(TAG, "User joined group")
            } else {
                Log.d(TAG, "No user logged in. Cannot join group.")
            }
        }

        fun leaveGroup(group: Group){
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child("users").child("groups").child(group.uid).removeValue()
                database.child("groups").child(group.uid).child("users").child(currentUserId)
                    .removeValue()
                group.rooms?.forEach {
                    database.child("rooms").child(it.value).child("users").child(currentUserId).removeValue()
                }
            } else {
                Log.d(TAG, "No user logged in. Cannot leave group.")
            }
        }

        fun deleteGroup(group: Group) {
            database.child("groups").child(group.uid).removeValue()
            for (user in group.users) {
                database.child("users").child("groups").child(group.uid).removeValue()
            }
            group.rooms?.forEach {
                database.child("rooms").child(it.value).removeValue()
            }
        }

        fun setGroupDescription(groupId: String, description: String) {
            database.child("groups").child(groupId).child("description").setValue(description)
        }

        fun saveRoom(groupId: String, roomType: Roomtype?) : String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val roomId = database.child("rooms").push().key
                if (roomId != null) {
                    val newRoom = Room(roomId, hashMapOf(currentUserId to currentUserId), roomType?: Roomtype.COFFEE)
                    database.child("rooms").child(roomId).setValue(newRoom)
                    database.child("groups").child(groupId).child("rooms").child(roomId).setValue(roomId)
                    Log.d(TAG, "Saved new room")
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
                database.child("rooms").child(roomId).child("users").child(currentUserId).setValue(currentUserId)
                Log.d(TAG, "User joined room")
            } else {
                Log.d(TAG, "No user logged in. Cannot join room.")
            }
        }

        fun leaveRoom(room: Room) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                if (room.users.size == 1 && room.users.containsKey(currentUserId)) {
                    database.child("rooms").child(room.uid).removeValue()
                    Log.d(TAG, "Deleted empty room.")
                } else {
                    database.child("rooms").child(room.uid).child("users").child(currentUserId)
                        .removeValue()
                    Log.d(TAG, "Removed user from room")
                }
            } else {
                Log.d(TAG, "No user logged in. Cannot leave room.")
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
                    database.child("users").child(currentUserId).child("friends").child(id).setValue(id)
                }
            } else {
                Log.d(TAG, "No user logged in. Cannot save add friends.")
            }
        }

        fun removeFriend(friendId: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child("users").child(currentUserId).child("friends").child(friendId).removeValue()
            } else {
                Log.d(TAG, "No user logged in. Cannot save remove friend.")
            }
        }

    }
}