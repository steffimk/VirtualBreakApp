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

    private val valueEventListener = object : ValueEventListener {

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

            private val queryFriends = PullData.database.child("users").child(PullData.currentUser.value?.uid ?: "").child("friends")

            override fun onActive() {
                super.onActive()
                queryFriends.addValueEventListener(valueEventListener)
            }

            override fun onInactive() {
                super.onInactive()
                queryFriends.removeEventListener(valueEventListener)
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
}