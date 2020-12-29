package com.example.virtualbreak.view.view_fragments.groupsfriends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.FriendListAdapter
import kotlinx.android.synthetic.main.fragment_groups_friendlist_fragment.*


class Groups_friendlist_fragment : Fragment() {


    companion object {
        fun newInstance() = Groups_friendlist_fragment()
    }

    private lateinit var groupsViewModel: GroupsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups_friendlist_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        groupsViewModel = ViewModelProvider(this).get(GroupsViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friends_recyler_list_view.layoutManager = LinearLayoutManager(activity)
        friends_recyler_list_view.adapter = FriendListAdapter()

        friends_add_friends_button.setOnClickListener{
            //navigate to add friends fragment
            view.findNavController().navigate(R.id.action_nav_home_to_addFriendsFragment)

        }
    }


}