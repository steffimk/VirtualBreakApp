package com.example.virtualbreak.controller.communication

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.virtualbreak.R
import com.example.virtualbreak.model.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream

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

        fun saveGroup(description: String, userIds: Array<String>?) : String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val groupId = database.child("groups").push().key
                if (groupId != null) {
                    val newGroup = Group(groupId, hashMapOf(currentUserId to currentUserId), description)
                    database.child("groups").child(groupId).setValue(newGroup)
                    database.child("users").child(currentUserId).child("groups").child(groupId).setValue(groupId)
                    userIds?.forEach{
                        joinGroup(groupId, it)
                    }
                    Log.d(TAG, "Saved new group")
                }
                return groupId
            } else {
                Log.d(TAG, "No user logged in. Cannot save group.")
                return null
            }
        }

        fun joinGroup(groupId: String, userId: String) {
            database.child("groups").child(groupId).child("users").child(userId).setValue(userId)
            database.child("users").child(userId).child("groups").child(groupId).setValue(groupId)
            Log.d(TAG, "User $userId joined group $groupId")
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

        fun saveRoom(groupId: String, roomType: Roomtype?, roomDescription: String) : String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val roomId = database.child("rooms").push().key
                if (roomId != null) {
                    val newRoom = Room(roomId, groupId, roomDescription, hashMapOf(currentUserId to currentUserId), HashMap(), roomType?: Roomtype.COFFEE)
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

        fun leaveRoom(room: Room?) {
            if (room == null) return

            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                if (room.users.size == 1 && room.users.containsKey(currentUserId)) {
                    database.child("groups").child(room.groupId).child("rooms").child(room.uid).removeValue()
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

        fun sendMessage(roomId: String, message: String){
            val currentUserId = Firebase.auth.currentUser?.uid
            if(currentUserId != null){
                val newChatMessage = Message(currentUserId, message)
                database.child("rooms").child(roomId).child("messages").push().setValue(newChatMessage)
            } else {
                Log.d(TAG, "No user logged in. Cannot send message.")
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
            // TODO: encode picture with Base64
//            val byteArrayOutputStream = ByteArrayOutputStream()
//            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.user)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//            val imageInBytes: ByteArray = byteArrayOutputStream.toByteArray()
//            val imageAsString: String = Base64.encodeToString(imageInBytes, Base64.DEFAULT)

            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child("users").child(currentUserId).child("profilePicture").setValue(picture)
            } else {
                Log.d(TAG, "No user logged in. Cannot save profile picture.")
            }
        }

        fun sendFriendRequest(friendId: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child("users").child(currentUserId).child("friendRequests").child(friendId).setValue(false)
                database.child("users").child(friendId).child("friendRequests").child(currentUserId).setValue(true)
            } else {
                Log.d(TAG, "No user logged in. Cannot send friend request.")
            }
        }

        /**
         * Before calling this function, check whether user really got friend request of user with passed on id
         */
        fun confirmFriendRequest(friendId: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                // add users to each others friends
                database.child("users").child(currentUserId).child("friends").child(friendId).setValue(friendId)
                database.child("users").child(friendId).child("friends").child(currentUserId).setValue(currentUserId)
                // Remove users from each others friend requests
                database.child("users").child(currentUserId).child("friendRequests").child(friendId).removeValue()
                database.child("users").child(friendId).child("friendRequests").child(currentUserId).removeValue()
            } else {
                Log.d(TAG, "No user logged in. Cannot confirm friend request.")
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