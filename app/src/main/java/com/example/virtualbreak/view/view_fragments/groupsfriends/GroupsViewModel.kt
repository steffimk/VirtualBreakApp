package com.example.virtualbreak.view.view_fragments.groupsfriends

import android.util.Log
import androidx.lifecycle.*
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Group
import com.example.virtualbreak.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class GroupsViewModel : ViewModel() {

    private val TAG = "GroupsViewModel"

    // ---------------------------------- FRIENDS ----------------------------------------
    private val friendsValueEventListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val pulledFriends = dataSnapshot.getValue<HashMap<String,String>>()

            if (pulledFriends == null) {
                Log.d(TAG, "Pulled Friends are null")
                return
            }

            Log.d(TAG, "Pulled Friends $pulledFriends")
            // TODO: Maybe check which friends got removed instead of clearing HashMaps
            friends.value?.clear()
            friends.value = friends.value

            pulledFriends.forEach() {
                    (key, userId) -> pullUserWithId(userId)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }
    }

    private val friends: MutableLiveData<HashMap<String,User>> =
        object : MutableLiveData<HashMap<String,User>>(HashMap()) {

            private val queryFriends = PullData.database.child("users").child(SharedPrefManager.instance.getUserId() ?: "").child("friends")

            override fun onActive() {
                super.onActive()
                queryFriends.addValueEventListener(friendsValueEventListener)
            }

            override fun onInactive() {
                super.onInactive()
                queryFriends.removeEventListener(friendsValueEventListener)
            }

        }

    fun getFriends(): LiveData<HashMap<String,User>> {
        return friends
    }

    private fun pullUserWithId(userId: String) {
        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    friends.value?.put(userId, user)
                    friends.value = friends.value // Set value so that observers are notified of change
                }
                Log.d(TAG, "Pulled User: $user")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }
        PullData.database.child("users").child(userId).addListenerForSingleValueEvent(valueEventListener)
    }

    // ---------------------------------- Groups ----------------------------------------

    private val groupsValueEventListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val pulledGroups = dataSnapshot.getValue<HashMap<String,String>>()

            if (pulledGroups == null) {
                Log.d(TAG, "Pulled Groups are null")
                return
            }

            Log.d(TAG, "Pulled Friends $pulledGroups")
            groups.value?.clear()
            groups.value = groups.value

            pulledGroups.forEach() {
                    (key, groupId) -> pullGroupWithId(groupId)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }
    }

    private val groups: MutableLiveData<HashMap<String,Group>> =
        object : MutableLiveData<HashMap<String,Group>>(HashMap()) {

            private val queryGroups = PullData.database.child("users").child(SharedPrefManager.instance.getUserId() ?: "").child("groups")

            override fun onActive() {
                super.onActive()
                queryGroups.addValueEventListener(groupsValueEventListener)
            }

            override fun onInactive() {
                super.onInactive()
                queryGroups.removeEventListener(groupsValueEventListener)
            }
        }

    fun getGroups(): LiveData<HashMap<String,Group>> {
        return groups
    }

    private fun pullGroupWithId(groupId: String) {
        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val group = dataSnapshot.getValue(Group::class.java)
                if (group != null) {
                    groups.value?.put(groupId, group)
                    groups.value = groups.value // Set value so that observers are notified of change
                }
                Log.d(TAG, "Pulled Group: $group")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }
        PullData.database.child("groups").child(groupId).addListenerForSingleValueEvent(valueEventListener)
    }

}