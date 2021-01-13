package com.example.virtualbreak.view.view_fragments.myprofile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class MyProfileViewModel : ViewModel() {

    private val TAG = "MyProfileViewModel"

    private val _text = MutableLiveData<String>().apply {
        value = "This is MyProfile Fragment"
    }
    val text: LiveData<String> = _text

    private val user: MutableLiveData<User> = object : MutableLiveData<User>() {
        private val userQuery = PullData.database.child(Constants.DATABASE_CHILD_USERS).child(SharedPrefManager.instance.getUserId() ?: "")

        override fun onActive() {
            super.onActive()
            userQuery.addValueEventListener(userValueEventListener)
        }

        override fun onInactive() {
            super.onInactive()
            userQuery.removeEventListener(userValueEventListener)
        }
    }

    fun getUser(): LiveData<User> {
        return user
    }

    private val userValueEventListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val pulledUser = dataSnapshot.getValue<User>()
            Log.d(TAG, "Pulled User $pulledUser")
            if (pulledUser != null) {
                SharedPrefManager.instance.saveUserName(pulledUser.username)
                user.value = pulledUser
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }

    }
}