package com.example.virtualbreak.view.view_fragments.friendrequests

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class FriendRequestsViewModel : ViewModel() {

    private val TAG = "FriendRequestsViewModel"

    private val incomingFriendRequests: MutableLiveData<HashMap<String,User>> =
        MutableLiveData<HashMap<String,User>>(HashMap()).also { pullFriends() }

    private val outgoingFriendRequests: MutableLiveData<HashMap<String,User>> =
        MutableLiveData<HashMap<String,User>>(HashMap())

    fun getIncomingFriendRequests(): LiveData<HashMap<String,User>> {
        return incomingFriendRequests
    }

    fun getOutGoingFriendRequests(): LiveData<HashMap<String,User>> {
        return outgoingFriendRequests
    }

    fun pullFriends() {

        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val friendRequests = dataSnapshot.getValue<HashMap<String,Boolean>>()

                if (friendRequests == null) {
                    Log.d(TAG, "Pulled FriendRequests are null")
                    return
                }

                Log.d(TAG, "Pulled FriendRequests $friendRequests")
                // TODO: Maybe check which friendRequests got removed instead of clearing HashMaps
                incomingFriendRequests.value?.clear()
                incomingFriendRequests.value = incomingFriendRequests.value
                outgoingFriendRequests.value?.clear()
                outgoingFriendRequests.value = outgoingFriendRequests.value

                friendRequests.forEach {
                    (userId, isIncoming) -> pullUserWithId(userId, isIncoming)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }

        val userId = PullData.currentUser.value?.uid
        if (userId != null) {
            PullData.database.child("users").child(userId).child("friendRequests").addValueEventListener(valueEventListener)
        }


    }

    private fun pullUserWithId(userId: String, isIncoming: Boolean) {
        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    if (isIncoming) {
                        incomingFriendRequests.value?.put(userId, user)
                        incomingFriendRequests.value = incomingFriendRequests.value // Set value so that observers are notified of change
                    } else {
                        outgoingFriendRequests.value?.put(userId, user)
                        outgoingFriendRequests.value = outgoingFriendRequests.value // Set value so that observers are notified of change
                    }
                }
                Log.d(TAG, "Pulled User: $user")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }
        PullData.database.child("users").child(userId).addListenerForSingleValueEvent(valueEventListener)
    }
}