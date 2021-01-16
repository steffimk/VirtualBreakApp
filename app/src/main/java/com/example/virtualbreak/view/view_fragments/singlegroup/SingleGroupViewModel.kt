package com.example.virtualbreak.view.view_fragments.singlegroup

import android.util.Log
import androidx.lifecycle.*
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Group
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class SingleGroupViewModel(private val groupId: String): ViewModel() {

    private val TAG = "SingleGroupViewModel"

    private val _text = MutableLiveData<String>().apply {
        value = "Offene Pausenräume:"
    }
    val text: LiveData<String> = _text

    private val rooms : MutableLiveData<HashMap<String,Room>> =
        object : MutableLiveData<HashMap<String,Room>>(HashMap()) {
            private val queryRooms = PullData.database.child(Constants.DATABASE_CHILD_GROUPS)
                .child(groupId).child(Constants.DATABASE_CHILD_ROOMS)

            override fun onActive() {
                super.onActive()
                queryRooms.addValueEventListener(roomsValueEventListener)
            }

            override fun onInactive() {
                super.onInactive()
                queryRooms.removeEventListener(roomsValueEventListener)
            }
        }

    fun getRooms(): LiveData<HashMap<String, Room>> {
        return rooms
    }

    private val roomsValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val pulledRooms = dataSnapshot.getValue<HashMap<String,String>>()

            Log.d(TAG, "Pulled Rooms $pulledRooms")

            rooms.value?.clear()
            rooms.value = rooms.value

            pulledRooms?.forEach() {
                    (key, roomId) -> pullRoomWithId(roomId)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }
    }

    private fun pullRoomWithId(roomId: String) {
        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val room = dataSnapshot.getValue(Room::class.java)
                if (room != null) {
                    rooms.value?.put(roomId, room)
                    rooms.value = rooms.value // Set value so that observers are notified of change
                }
                Log.d(TAG, "Pulled Room: $room")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }
        PullData.database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).addListenerForSingleValueEvent(valueEventListener)
    }

    private val user: MutableLiveData<User> = object : MutableLiveData<User>() {
        private val userQuery = PullData.database.child(Constants.DATABASE_CHILD_USERS).child(
            SharedPrefManager.instance.getUserId() ?: "")

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

            user.value = pulledUser
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }

    }

    var currentGroup: Group? = null

    fun pullGroupWithId(groupId: String) {
        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var group = dataSnapshot.getValue(Group::class.java)!!
                if (group != null) {
                    currentGroup = group
                }
                Log.d(TAG, "Pulled Current Group")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }
        PullData.database.child(Constants.DATABASE_CHILD_GROUPS).child(groupId)
            .addListenerForSingleValueEvent(valueEventListener)
    }

}