package com.example.virtualbreak.view.view_fragments.addfriends

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
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue


class AddFriendsViewModel : ViewModel() {

    private val TAG = "AddFriendsViewModel"

    private val currentUser: MutableLiveData<User> = MutableLiveData<User>().also {
        PullData.database.child(Constants.DATABASE_CHILD_USERS).child(SharedPrefManager.instance.getUserId() ?: "")
            .addValueEventListener(userValueEventListener)
    }
    private val searchedUser: MutableLiveData<User?> = MutableLiveData(null)

    fun getSearchedUser(): LiveData<User?> {
        return searchedUser
    }

    fun getCurrentUser(): LiveData<User> {
        return currentUser
    }

    fun searchForUserWithFullEmail(email: String) {

        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataType: GenericTypeIndicator<HashMap<String,User?>?> =
                    object : GenericTypeIndicator<HashMap<String,User?>?>() {}

                val userList = dataSnapshot.getValue(dataType)
                val user = userList?.get(userList.keys.firstOrNull())
                searchedUser.value = user
                if (user != null){
                    Log.d(TAG, "Pulled User With Mail: $user")
                } else {
                    Log.d(TAG, "No User With Matching Mail")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }

        searchedUser.value = null
        PullData.database.child(Constants.DATABASE_CHILD_USERS).orderByChild("email").equalTo(email).limitToFirst(1).addListenerForSingleValueEvent(
            valueEventListener
        )
    }

    private val userValueEventListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val pulledUser = dataSnapshot.getValue<User>()
            Log.d(TAG, "Pulled User $pulledUser")

            currentUser.value = pulledUser
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }

    }

    override fun onCleared() {
        super.onCleared()
        PullData.database.child(Constants.DATABASE_CHILD_USERS).child(SharedPrefManager.instance.getUserId() ?: "")
            .removeEventListener(userValueEventListener)
    }
}