package com.example.virtualbreak.controller.communication

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
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
                  database.child(Constants.DATABASE_CHILD_USERS).child(user.uid).setValue(userData)
                Log.d(TAG, "Saved user to realtime database")
            } else {
                Log.d(TAG, "No user logged in. Cannot save user.")
            }
        }

        fun saveGroup(description: String, userIds: Array<String>?) : String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val groupId = database.child(Constants.DATABASE_CHILD_GROUPS).push().key
                if (groupId != null) {
                    val newGroup = Group(groupId, hashMapOf(currentUserId to currentUserId), description)
                    database.child(Constants.DATABASE_CHILD_GROUPS).child(groupId).setValue(newGroup)
                    database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_GROUPS).child(groupId).setValue(groupId)
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
            database.child(Constants.DATABASE_CHILD_GROUPS).child(groupId).child(Constants.DATABASE_CHILD_USERS).child(userId).setValue(userId)
            database.child(Constants.DATABASE_CHILD_USERS).child(userId).child(Constants.DATABASE_CHILD_GROUPS).child(groupId).setValue(groupId)
            Log.d(TAG, "User $userId joined group $groupId")
        }

        fun leaveGroup(group: Group){
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(Constants.DATABASE_CHILD_GROUPS).child(group.uid).removeValue()
                database.child(Constants.DATABASE_CHILD_GROUPS).child(group.uid).child(Constants.DATABASE_CHILD_USERS).child(currentUserId)
                    .removeValue()
                group.rooms?.forEach {
                    database.child(Constants.DATABASE_CHILD_ROOMS).child(it.value).child(Constants.DATABASE_CHILD_USERS).child(currentUserId).removeValue()
                }
            } else {
                Log.d(TAG, "No user logged in. Cannot leave group.")
            }
        }

        fun deleteGroup(group: Group) {
            database.child(Constants.DATABASE_CHILD_GROUPS).child(group.uid).removeValue()
            for (user in group.users) {
                database.child(Constants.DATABASE_CHILD_USERS).child(Constants.DATABASE_CHILD_GROUPS).child(group.uid).removeValue()
            }
            group.rooms?.forEach {
                database.child(Constants.DATABASE_CHILD_ROOMS).child(it.value).removeValue()
            }
        }

        fun setGroupDescription(groupId: String, description: String) {
            database.child(Constants.DATABASE_CHILD_GROUPS).child(groupId).child(Constants.DATABASE_CHILD_DESCRIPTION).setValue(description)
        }

        fun saveRoom(groupId: String, roomType: Roomtype?, roomDescription: String) : String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val roomId = database.child(Constants.DATABASE_CHILD_ROOMS).push().key
                if (roomId != null) {
                    val newRoom = Room(roomId, groupId, roomDescription, hashMapOf(currentUserId to currentUserId), HashMap(), roomType?: Roomtype.COFFEE)
                    database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).setValue(newRoom)
                    database.child(Constants.DATABASE_CHILD_GROUPS).child(groupId).child(Constants.DATABASE_CHILD_ROOMS).child(roomId).setValue(roomId)
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
                database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_USERS).child(currentUserId).setValue(currentUserId)
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
                    database.child(Constants.DATABASE_CHILD_GROUPS).child(room.groupId).child(Constants.DATABASE_CHILD_ROOMS).child(room.uid).removeValue()
                    database.child(Constants.DATABASE_CHILD_ROOMS).child(room.uid).removeValue()
                    Log.d(TAG, "Deleted empty room.")
                } else {
                    database.child(Constants.DATABASE_CHILD_ROOMS).child(room.uid).child(Constants.DATABASE_CHILD_USERS).child(currentUserId)
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
                database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_MESSAGES).push().setValue(newChatMessage)
            } else {
                Log.d(TAG, "No user logged in. Cannot send message.")
            }
        }

        fun setStatus(status: Status) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_STATUS).setValue(status)
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
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_PROFILE_PICTURE).setValue(picture)
            } else {
                Log.d(TAG, "No user logged in. Cannot save profile picture.")
            }
        }

        fun sendFriendRequest(friendId: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_FRIEND_REQUESTS).child(friendId).setValue(false)
                database.child(Constants.DATABASE_CHILD_USERS).child(friendId).child(Constants.DATABASE_CHILD_FRIEND_REQUESTS).child(currentUserId).setValue(true)
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
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_FRIENDS).child(friendId).setValue(friendId)
                database.child(Constants.DATABASE_CHILD_USERS).child(friendId).child(Constants.DATABASE_CHILD_FRIENDS).child(currentUserId).setValue(currentUserId)
                // Remove users from each others friend requests
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_FRIEND_REQUESTS).child(friendId).removeValue()
                database.child(Constants.DATABASE_CHILD_USERS).child(friendId).child(Constants.DATABASE_CHILD_FRIEND_REQUESTS).child(currentUserId).removeValue()
            } else {
                Log.d(TAG, "No user logged in. Cannot confirm friend request.")
            }
        }

        fun removeFriend(friendId: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_FRIENDS).child(friendId).removeValue()
            } else {
                Log.d(TAG, "No user logged in. Cannot save remove friend.")
            }
        }

    }
}