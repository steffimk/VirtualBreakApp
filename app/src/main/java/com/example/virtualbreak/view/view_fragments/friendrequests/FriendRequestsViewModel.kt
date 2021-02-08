package com.example.virtualbreak.view.view_fragments.friendrequests

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

/**
 * ViewModel of the FriendRequests Fragment
 */
class FriendRequestsViewModel : ViewModel() {

    private val TAG = "FriendRequestsViewModel"

    /**
     * LiveData containing a hashmap of the user ids and users of incoming friend requests
     */
    private val incomingFriendRequests: MutableLiveData<HashMap<String,User>> =
        object : MutableLiveData<HashMap<String,User>>(HashMap()) {

            private val queryFriendRequests = PullData.database.child(Constants.DATABASE_CHILD_USERS).child(SharedPrefManager.instance.getUserId() ?: "").child(Constants.DATABASE_CHILD_FRIEND_REQUESTS)

            override fun onActive() {
                super.onActive()
                queryFriendRequests.addValueEventListener(valueEventListener)
            }

            override fun onInactive() {
                super.onInactive()
                queryFriendRequests.removeEventListener(valueEventListener)
            }
        }

    /**
     * LiveData containing a hashmap of the user ids and users of outgoing friend requests
     */
    private val outgoingFriendRequests: MutableLiveData<HashMap<String,User>> =
        MutableLiveData<HashMap<String,User>>(HashMap())

    /**
     * Value event listener of the friend requests of the own user.
     * When friend requests change: Pull user of each friend request and check whether
     * friend request is incoming or outgoing
     */
    private val valueEventListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val friendRequests = dataSnapshot.getValue<HashMap<String,Boolean>>()

            incomingFriendRequests.value?.clear()
            incomingFriendRequests.value = incomingFriendRequests.value
            outgoingFriendRequests.value?.clear()
            outgoingFriendRequests.value = outgoingFriendRequests.value

            if (friendRequests == null) {
                Log.d(TAG, "Pulled FriendRequests are null")
                return
            }

            Log.d(TAG, "Pulled FriendRequests $friendRequests")

            friendRequests.forEach {
                    (userId, isIncoming) -> pullUserWithId(userId, isIncoming)
            }

        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }
    }

    /**
     * Returns LiveData containing the incoming friend requests in form of a hashmap
     * @return LiveData with a hashmap mapping user ids to users
     */
    fun getIncomingFriendRequests(): LiveData<HashMap<String,User>> {
        return incomingFriendRequests
    }

    /**
     * Returns LiveData containing the outgoing friend requests in form of a hashmap
     * @return LiveData with a hashmap mapping user ids to users
     */
    fun getOutgoingFriendRequests(): LiveData<HashMap<String,User>> {
        return outgoingFriendRequests
    }

    /**
     * Pulls the user with the given id and saves it in incomingFriendRequests or outgoingFriendRequests
     * @param userId The id of the user to be pulled
     * @param isIncoming Informs whether the pulled user is an incoming or an outgoing friendRequest
     */
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
        PullData.database.child(Constants.DATABASE_CHILD_USERS).child(userId).addListenerForSingleValueEvent(valueEventListener)
    }

    /**
     * Removes all event listeners
     */
    override fun onCleared() {
        super.onCleared()
        PullData.database.child(Constants.DATABASE_CHILD_USERS).child(SharedPrefManager.instance.getUserId() ?: "")
            .child(Constants.DATABASE_CHILD_FRIEND_REQUESTS).removeEventListener(valueEventListener)
    }
}