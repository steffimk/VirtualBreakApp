package com.example.virtualbreak.view.view_fragments.singlegroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SingleGroupViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Offene Pausenräume:"
    }
    val text: LiveData<String> = _text
}