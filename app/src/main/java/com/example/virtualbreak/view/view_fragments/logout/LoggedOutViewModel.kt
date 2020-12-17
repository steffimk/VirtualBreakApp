package com.example.virtualbreak.view.view_fragments.logout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoggedOutViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Augelogged"
    }
    val text: LiveData<String> = _text
}