package com.example.virtualbreak.view.view_fragments.question

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


class QuestionViewModel(roomId: String) : ViewModel() {

    private val TAG = "QuestionFragmentViewModel"
    private val roomId = roomId

    private val question: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>(null).also {
            PullData.database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_QUESTION)
                .addValueEventListener(questionEventListener)
        }
    }

    fun getQuestion(): LiveData<String?> {
        return question
    }

    private val questionEventListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val pulledQuestion = dataSnapshot.getValue<String>()
            Log.d(TAG, "Pulled Question $pulledQuestion")

            question.value = pulledQuestion
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }

    }

    override fun onCleared() {
        super.onCleared()
        PullData.database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_QUESTION)
            .removeEventListener(questionEventListener)
    }
}

class QuestionFragmentViewModelFactory(private val roomId: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return QuestionViewModel(roomId) as T
    }

}