package com.example.virtualbreak.view.view_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.GroupsListAdapter
import kotlinx.android.synthetic.main.fragment_groups_grouplist_fragment.*
import com.example.virtualbreak.view.view_models.GroupsViewModel as GroupsViewModel


class Groups_grouplist_fragment : Fragment() {

    companion object {
        fun newInstance() = Groups_grouplist_fragment()
    }

    private lateinit var viewModel: GroupsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups_grouplist_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GroupsViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groups_recyler_list_view.layoutManager = LinearLayoutManager(activity)
        groups_recyler_list_view.adapter = GroupsListAdapter()
    }



}