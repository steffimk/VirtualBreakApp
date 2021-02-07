package com.example.virtualbreak.view.view_fragments.textchat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson

class TextchatViewModel(private val roomId: String) : ViewModel() {
    private val TAG = "TextchatViewModel"

    private val room: MutableLiveData<Room> = object : MutableLiveData<Room>() {
        private val queryRoom =
            PullData.database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId)

        override fun onActive() {
            super.onActive()
            queryRoom.addValueEventListener(roomValueEventListener)
        }

        override fun onInactive() {
            super.onInactive()
            queryRoom.removeEventListener(roomValueEventListener)
        }
    }

    /**
     * Getting the current room
     * @return the Room object
     */
    fun getRoom(): LiveData<Room> {
        return room
    }

    /**
     * onDataChange listener on room
     */
    private val roomValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val pulledRoom = dataSnapshot.getValue<Room>()
            Log.d(TAG, "Pulled Room $pulledRoom")

            room.value = pulledRoom
            loadUsersOfRoom()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }
    }

    /**
     * Loads users of the room and store them in shared preferences
     */
    fun loadUsersOfRoom() {
        //some logic so that usernames are added to hashmap saved in SharedPrefs, but none can be removed, if someone leaves the room
        var usersOfRoom: HashMap<String, String>? = HashMap()
        if (SharedPrefManager.instance.getRoomUsersHashmap() != null) {
            usersOfRoom = SharedPrefManager.instance.getRoomUsersHashmap()
        }

        Log.d(TAG, "loadUsersOfRoom")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                val name = user!!.username
                Log.d(TAG, "User added: " + name)

                usersOfRoom?.put(dataSnapshot.key.toString(), name) //update user + name

                //convert to string using gson
                val gson = Gson()
                val hashMapString = gson.toJson(usersOfRoom)

                //SharedPrefManager.instance.removeRoomUsers()
                SharedPrefManager.instance.saveRoomUsers(hashMapString) //save hashmap in shared prefs
                room.value = room.value // notify observers of room to change
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }

        val users = room.value?.users
        if (users != null) {
            for (u in users) {
                PullData.database.child(Constants.DATABASE_CHILD_USERS).child(u.key)
                    .addListenerForSingleValueEvent(valueEventListener)
            }
        }
    }
}

class TextchatViewModelFactory(private val roomId: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TextchatViewModel(roomId) as T
    }

}