package com.example.virtualbreak.view.ui.myprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is MyProfile Fragment"
    }
    val text: LiveData<String> = _text
}