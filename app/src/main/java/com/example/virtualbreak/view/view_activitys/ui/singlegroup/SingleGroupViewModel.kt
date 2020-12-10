package com.example.virtualbreak.view.view_activitys.ui.singlegroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SingleGroupViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is SingleGroup Fragment"
    }
    val text: LiveData<String> = _text
}