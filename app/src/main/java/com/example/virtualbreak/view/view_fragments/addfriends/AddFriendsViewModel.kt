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

/**
 * ViewModel of the AddFriendsFragment
 */
class AddFriendsViewModel : ViewModel() {

    private val TAG = "AddFriendsViewModel"

    private val currentUser: MutableLiveData<User> by lazy {
        MutableLiveData<User>().also {
            PullData.database.child(Constants.DATABASE_CHILD_USERS).child(SharedPrefManager.instance.getUserId() ?: "")
                .addValueEventListener(userValueEventListener)
        }
    }
    private val searchedUser: MutableLiveData<User?> = MutableLiveData(null)

    /**
     * Returns an instance of LiveData containing the user found when looking for the entered mail address
     * @return LiveData containing the searched user if it was found. Contains null if there is no user with this mail
     */
    fun getSearchedUser(): LiveData<User?> {
        return searchedUser
    }

    /**
     * Returns an instance of LiveData containing the own user
     * @return LiveData containing the own user
     */
    fun getCurrentUser(): LiveData<User> {
        return currentUser
    }

    /**
     * Searches the database for a user with a mail matching the passed String
     * @param email The mail of the user that is to be searched
     */
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

        val mailInLowerCase = email.toLowerCase()
        searchedUser.value = null
        PullData.database.child(Constants.DATABASE_CHILD_USERS).orderByChild(Constants.DATABASE_CHILD_EMAIL)
            .equalTo(mailInLowerCase).limitToFirst(1).addListenerForSingleValueEvent(valueEventListener)
    }

    /**
     * Saves pulled data in currentUser
     */
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

    /**
     * Remove all event listeners
     */
    override fun onCleared() {
        super.onCleared()
        PullData.database.child(Constants.DATABASE_CHILD_USERS).child(SharedPrefManager.instance.getUserId() ?: "")
            .removeEventListener(userValueEventListener)
    }
}