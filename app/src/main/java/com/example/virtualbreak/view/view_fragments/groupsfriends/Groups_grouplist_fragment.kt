package com.example.virtualbreak.view.view_fragments.groupsfriends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.adapters.groupsfriends.GroupsListAdapter

import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Group
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_groups_grouplist_fragment.*
import com.example.virtualbreak.view.view_fragments.groupsfriends.GroupsViewModel as GroupsViewModel


class Groups_grouplist_fragment : Fragment() {

    companion object {
        fun newInstance() = Groups_grouplist_fragment()
    }

    private lateinit var viewModel: GroupsViewModel
    private val TAG = "Group_GroupList_Fragment"

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
        //groups_recyler_list_view.adapter = GroupsListAdapter()

        PullData.groups.observe(viewLifecycleOwner, Observer<HashMap<String, Group>> { groupsMap ->
            Log.d(TAG, "Observing Group")
            if (groupsMap != null) {
                groups_recyler_list_view.adapter = GroupsListAdapter(ArrayList(PullData.groups.value?.values)) // Completely new adapter (TODO: Maybe reuse and adapt previous one)
            }
        })

        groups_add_group_button.setOnClickListener{
            //TODO Add Groups
            //Snackbar.make(view, "Öffne neuen Pausenraum", Snackbar.LENGTH_LONG)
            //   .setAction("Action", null).show()
            //itemView.findNavController().navigate(R.id.action_nav_home_to_singleGroupFragment)
            view.findNavController().navigate(R.id.action_nav_home_to_addGroupFragment)
        }
    }



}