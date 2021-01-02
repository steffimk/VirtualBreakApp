package com.example.virtualbreak.view.view_fragments.groupsfriends

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

class GroupsViewModel : ViewModel() {

    private val TAG = "GroupsViewModel"

    private val friends: MutableLiveData<HashMap<String,User>> =
        MutableLiveData<HashMap<String,User>>(HashMap()).also { pullFriends() }

    fun getFriends(): LiveData<HashMap<String,User>> {
        return friends
    }

    private fun pullFriends() {

        val valueEventListener = object : ValueEventListener {

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

        val userId = PullData.currentUser.value?.uid
        if (userId != null) {
            PullData.database.child("users").child(userId).child("friends").addValueEventListener(valueEventListener)
        }
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
}