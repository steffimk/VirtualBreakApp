package com.example.virtualbreak.view.view_fragments.singlegroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Use this class to get a new SingleGroupViewModel
 */
class SingleGroupViewModelFactory(private val groupId: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SingleGroupViewModel(groupId) as T
    }

}