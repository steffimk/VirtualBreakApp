package com.example.virtualbreak.view.view_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.FriendListAdapter
import com.example.virtualbreak.controller.adapters.GroupsListAdapter
import com.example.virtualbreak.view.view_models.GroupsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_groups_friendlist_fragment.*
import kotlinx.android.synthetic.main.fragment_groups_grouplist_fragment.*


class Groups_friendlist_fragment : Fragment() {

    companion object {
        fun newInstance() = Groups_friendlist_fragment()
    }

    private lateinit var viewModel: GroupsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups_friendlist_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GroupsViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friends_recyler_list_view.layoutManager = LinearLayoutManager(activity)
        friends_recyler_list_view.adapter = FriendListAdapter()

        friends_add_friends_button.setOnClickListener{
            //TODO Add Friends
            Snackbar.make(view, "Füge Freunde hinzu", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }


}