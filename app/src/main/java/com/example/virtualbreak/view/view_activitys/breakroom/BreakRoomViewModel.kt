package com.example.virtualbreak.view.view_activitys.breakroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Room
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

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
}

class BreakRoomViewModelFactory(private val roomId: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BreakRoomViewModel(roomId) as T
    }

}