package com.example.virtualbreak.view.view_fragments.groupsfriends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import androidx.navigation.findNavController
import com.example.virtualbreak.controller.adapters.FriendListAdapter
import com.example.virtualbreak.controller.adapters.groupsfriends.GroupsListAdapter
import com.example.virtualbreak.model.Group
import kotlinx.android.synthetic.main.fragment_groups_friendlist_fragment.*
import kotlinx.android.synthetic.main.fragment_groups_grouplist_fragment.*
import com.example.virtualbreak.view.view_fragments.groupsfriends.GroupsViewModel as GroupsViewModel

/**
 * Groups list fragment in home fragment
 */
class Groups_grouplist_fragment : Fragment() {

    private val TAG = "Group_GroupList_Fragment"
    private var groupsListAdapter: GroupsListAdapter? = null

    companion object {
        fun newInstance() = Groups_grouplist_fragment()
    }

    private val viewModel: GroupsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups_grouplist_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groups_recyler_list_view.layoutManager = LinearLayoutManager(activity)

        viewModel.getGroups()
            .observe(viewLifecycleOwner, Observer<HashMap<String, Group>> { groups ->

                if(groups == null || groups.isEmpty() || groups.size == 0){
                    no_groups_yet_linearlayout.visibility = View.VISIBLE //display text that no groups were added yet
                } else{
                    no_groups_yet_linearlayout.visibility = View.GONE

                    //initialise/update list
                    var groupslist = ArrayList(groups.values)
                    groupslist.sortBy{it.description}
                    groupsListAdapter = GroupsListAdapter(groupslist, context)
                    groups_recyler_list_view.adapter = groupsListAdapter
                }

                Log.d(TAG, "Observed groups: $groups")
            })

        groups_add_group_button.setOnClickListener {
            view.findNavController().navigate(R.id.action_nav_home_to_addGroupFragment)
        }
    }

}