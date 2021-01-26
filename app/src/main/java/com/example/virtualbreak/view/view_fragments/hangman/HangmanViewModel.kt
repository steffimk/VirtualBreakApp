package com.example.virtualbreak.view.view_fragments.hangman

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Game
import com.example.virtualbreak.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson

class HangmanViewModel(private val gameId: String) : ViewModel() {
    private val TAG = "HangmanViewModel"


    private val game : MutableLiveData<Game> = object : MutableLiveData<Game>() {
        private val queryGame = PullData.database.child(Constants.DATABASE_CHILD_GAMES).child(gameId)

        override fun onActive() {
            super.onActive()
            queryGame.addValueEventListener(gameValueEventListener)
        }

        override fun onInactive() {
            super.onInactive()
            queryGame.removeEventListener(gameValueEventListener)
        }
    }

    fun getGame(): LiveData<Game> {
        return game
    }

    private val gameValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val pulledGame = dataSnapshot.getValue<Game>()
            Log.d(TAG, "Pulled Room $pulledGame")

            game.value = pulledGame
            game.value = game.value
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.message)
        }
    }

}

class HangmanViewModelFactory(private val gameId: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HangmanViewModel(gameId) as T
    }

}