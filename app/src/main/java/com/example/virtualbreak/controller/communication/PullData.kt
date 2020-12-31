package com.example.virtualbreak.controller.communication

import android.util.Log
import androidx.lifecycle.MutableLiveData
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

        private const val TAG: String = "PullData"

        val database : DatabaseReference = Firebase.database.reference
        var currentUser: MutableLiveData<User?> = MutableLiveData(null)
        var groups: MutableLiveData<HashMap<String,Group>> = MutableLiveData(HashMap())
        var rooms: MutableLiveData<HashMap<String,Room>> = MutableLiveData(HashMap())
        var friends: MutableLiveData<HashMap<String,User>> = MutableLiveData(HashMap())
        var incomingFriendRequests: MutableLiveData<HashMap<String,User>> = MutableLiveData(HashMap())

        fun getRoomsOfGroup(groupId: String) : ArrayList<Room> {
            val roomIdsOfGroup = groups.value?.get(groupId)?.rooms ?: return ArrayList()
            val roomsOfGroupMap =
                rooms.value?.filterKeys { roomId -> roomIdsOfGroup.containsKey(roomId) } ?: return ArrayList()
            return ArrayList(roomsOfGroupMap.values)
        }

        fun attachListenerToCurrentUser() {
            if (currentUser.value != null) {
                return // Listener already attached
            }

            val valueEventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val isFirstPull = currentUser.value == null
                    currentUser.value = dataSnapshot.getValue(User::class.java)
                    if (isFirstPull) {
                        reloadFriends()
                        reloadIncomingFriendRequests()
                    }
                    updateGroups()
                    Log.d(TAG, "Pulled User: " + currentUser.value)
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

        private fun updateGroups() {
            val pulledGroupIds = currentUser.value?.groups
            pulledGroupIds?.forEach{
                if (!groups.value?.containsKey(it.key)!!){
                    attachListenerToGroup(it.key)     // Attach Listeners to new groups
                }
            }
            groups.value?.forEach{
                if (pulledGroupIds != null) {
                    if (!pulledGroupIds.containsKey(it.key)) {  // Remove groups the user is not in anymore
                        groups.value?.remove(it.key)
                        groups.value = groups.value // Set value so that observers are notified of change
                    }
                } else {
                    groups.value = HashMap() // No group ids are pulled => empty HashMap
                }
            }
        }

        private fun attachListenerToGroup(groupId: String) {
            val valueEventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val group = dataSnapshot.getValue(Group::class.java)
                    if (group != null){
                        groups.value?.put(groupId, group)
                        groups.value = groups.value // Set value so that observers are notified of change
                        updateRoomsOfGroup(groupId)
                    }
                    Log.d(TAG, "Pulled Group: $group")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.message)
                }
            }
            database.child("groups").child(groupId).addValueEventListener(valueEventListener)
        }

        private fun updateRoomsOfGroup(groupId: String) {
            val pulledRoomIds = groups.value?.get(groupId)?.rooms ?: return

            // Attach listener to each newly pulled room
            pulledRoomIds.forEach{
                if (!rooms.value?.containsKey(it.key)!!){
                    attachListenerToRoom(it.key)     // Attach Listeners to new rooms
                }
            }

            // Remove rooms that got deleted from the database
            val iterator = rooms.value!!.iterator()
            while (iterator.hasNext()) {
                val room = iterator.next().value
                if (room.groupId == groupId && !pulledRoomIds.containsKey(room.uid)) {
                    iterator.remove()
                    // TODO: Remove listener!!
                }
            }
        }

        private fun attachListenerToRoom(roomId: String) {
            val valueEventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val room = dataSnapshot.getValue(Room::class.java)
                    if (room != null) {
                        rooms.value?.put(roomId, room)
                        rooms.value = rooms.value // Set value so that observers are notified of change
                    }
                    Log.d(TAG, "Pulled Room: $room")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.message)
                }
            }
            database.child("rooms").child(roomId).addValueEventListener(valueEventListener)
        }

        fun reloadFriends() {
            val friendIds = currentUser.value?.friends
            friends.value?.clear()  // Empty hashmap and reload friends
            friendIds?.forEach{
                user -> attachSingleEventListenerToUser(user.key, true)
            }
        }

        fun reloadIncomingFriendRequests() {
            val idsOfIncomingFriendRequests =
                currentUser.value?.friendRequests?.filterValues { isIncoming -> isIncoming }
            incomingFriendRequests.value?.clear() // Empty hashmap before reloading friend requests
            idsOfIncomingFriendRequests?.forEach {
                user -> attachSingleEventListenerToUser(user.key, false)
            }
        }

        /**
         * Reloads users from database. Set 'isFriend' true if you want to pull a friend
         * and false if you want to pull a user that sent a friend request
         */
        private fun attachSingleEventListenerToUser(userId: String, isFriend: Boolean) {
            val valueEventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        if (isFriend) {
                            friends.value?.put(userId, user)
                            friends.value = friends.value // Set value so that observers are notified of change
                        } else {
                            incomingFriendRequests.value?.put(userId, user)
                            incomingFriendRequests.value = incomingFriendRequests.value // Set value so that observers are notified of change
                        }
                    }
                    Log.d(TAG, "Pulled User: $user")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.message)
                }
            }
            database.child("users").child(userId).addListenerForSingleValueEvent(valueEventListener)
        }

    }
}


