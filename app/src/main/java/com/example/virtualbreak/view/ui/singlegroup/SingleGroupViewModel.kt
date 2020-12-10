package com.example.virtualbreak.view.ui.singlegroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SingleGroupViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is SingleGroup Fragment"
    }
    val text: LiveData<String> = _text
}