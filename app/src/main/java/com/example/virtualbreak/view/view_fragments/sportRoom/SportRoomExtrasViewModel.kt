package com.example.virtualbreak.view.view_fragments.sportRoom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.communication.PullData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

/**
 * ViewModel of the SportRoomExtras Fragment
 */
class SportRoomExtrasViewModel(roomId: String) : ViewModel() {

    private val TAG = "SportRoomExtrasViewModel"
    private val roomId = roomId

    /**
     * LiveData containing the timerEndDate
     */
    private val timerEndDate: MutableLiveData<Long?> by lazy {
        MutableLiveData<Long?>(null).also {
            PullData.database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_TIMER_END)
                .addValueEventListener(timerEventListener)
        }
    }

    /**
     * The fitness exercise that is currently displayed
     */
    var fitnessExercise: String? = null

    /**
     * Adds a value event listener to the exercise of the room
     */
    fun startPullingExercise() {
        if (fitnessExercise != null) return
        PullData.database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_EXERCISE)
            .addValueEventListener(exerciseEventListener)
    }

    /**
     * Returns LiveData containing the end date of the timer
     * @return Instance of LiveData containing the end date of the timer
     */
    fun getTimerEndDate(): LiveData<Long?> {
        return timerEndDate
    }

    /**
     * Saves the pulled timer in timerEndDate
     */
    private val timerEventListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val pulledTimerEnd = dataSnapshot.getValue<Long>()
            Log.d(TAG, "Pulled Timer $pulledTimerEnd")

            timerEndDate.value = pulledTimerEnd
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }

    }

    /**
     * Saves the pulled exercise in fitnessExercise
     */
    private val exerciseEventListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val pulledExercise = dataSnapshot.getValue<String>()
            Log.d(TAG, "Pulled Exercise $pulledExercise")

            fitnessExercise = pulledExercise
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }

    }

    /**
     * Removes all event listeners
     */
    override fun onCleared() {
        super.onCleared()
        PullData.database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_TIMER_END)
            .removeEventListener(timerEventListener)
        PullData.database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_EXERCISE)
            .removeEventListener(exerciseEventListener)
    }
}

/**
 * Use this factory to get a new instance of SportRoomExtrasViewModel
 */
class SportRoomExtrasViewModelFactory(private val roomId: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SportRoomExtrasViewModel(roomId) as T
    }

}