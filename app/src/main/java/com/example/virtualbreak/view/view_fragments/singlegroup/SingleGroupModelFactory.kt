package com.example.virtualbreak.view.view_fragments.singlegroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class SingleGroupViewModelFactory(private val groupId: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SingleGroupViewModel(groupId) as T
    }

}