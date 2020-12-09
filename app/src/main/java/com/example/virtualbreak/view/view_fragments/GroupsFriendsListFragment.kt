package com.example.virtualbreak.view.view_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.ViewPagerAdapter
import com.example.virtualbreak.view.view_models.GroupsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_groups_friends_list.*

class GroupsFriendsListFragment : Fragment() {

    private lateinit var viewModel: GroupsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_groups_friends_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GroupsViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupsfr_view_Pager.adapter = ViewPagerAdapter(activity)

        val tabNames:Array<String> = arrayOf(getString(R.string.tab_group), getString(R.string.tab_friends))

        TabLayoutMediator(groupsfr_tab_layout, groupsfr_view_Pager){tab, position ->
            tab.text = tabNames[position]
        }.attach()

        groupsfr_add_group_button.setOnClickListener{
            //TODO Go to create Group Fragment
        }

    }

}