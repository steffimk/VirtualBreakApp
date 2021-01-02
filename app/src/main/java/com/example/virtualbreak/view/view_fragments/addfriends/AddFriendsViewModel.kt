package com.example.virtualbreak.view.view_fragments.addfriends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener


class AddFriendsViewModel : ViewModel() {

    private val TAG = "AddFriendsViewModel"

    private val searchedUser: MutableLiveData<User?> = MutableLiveData(null)

    fun getSearchedUser(): LiveData<User?> {
        return searchedUser
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
        PullData.database.child("users").orderByChild("email").equalTo(email).limitToFirst(1).addListenerForSingleValueEvent(
            valueEventListener
        )
    }
}