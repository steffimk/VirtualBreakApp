package com.example.virtualbreak.view.view_activitys.breakroom

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Message
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson

class BreakRoomViewModel(private val roomId: String): ViewModel() {

    private val TAG = "BreakRoomViewModel"

    private val room : MutableLiveData<Room> = object : MutableLiveData<Room>() {
        private val queryRoom = PullData.database.child("rooms").child(roomId)

        override fun onActive() {
            super.onActive()
            queryRoom.addValueEventListener(roomValueEventListener)
        }

        override fun onInactive() {
            super.onInactive()
            queryRoom.removeEventListener(roomValueEventListener)
        }
    }

    fun getRoom(): LiveData<Room> {
        return room
    }

    private val roomValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val pulledRoom = dataSnapshot.getValue<Room>()
            Log.d(TAG, "Pulled Room $pulledRoom")

            room.value = pulledRoom
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }
    }

    fun loadUsersOfRoom(context: Context){
        val currentRoom = room.value
        val users = currentRoom?.users

        var usersOfRoom : HashMap<String,String> = HashMap()

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                val name = user!!.username
                usersOfRoom.put(dataSnapshot.key.toString(), name)

                //convert to string using gson
                val gson = Gson()
                val hashMapString = gson.toJson(usersOfRoom)

                SharedPrefManager.instance.removeRoomUsers()
                SharedPrefManager.instance.saveRoomUsers(hashMapString)
                //save hashmap in shared prefs
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }

        if (users != null) {
            for(u in users){
                PullData.database.child("users").child(u.key).addListenerForSingleValueEvent(valueEventListener)
            }
        }
    }
}


class BreakRoomViewModelFactory(private val roomId: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BreakRoomViewModel(roomId) as T
    }

}