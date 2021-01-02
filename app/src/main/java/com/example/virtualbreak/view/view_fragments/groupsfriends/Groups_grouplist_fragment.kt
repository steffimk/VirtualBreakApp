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
import com.example.virtualbreak.controller.adapters.GroupsListAdapter
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Group
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_groups_grouplist_fragment.*
import com.example.virtualbreak.view.view_fragments.groupsfriends.GroupsViewModel as GroupsViewModel


class Groups_grouplist_fragment : Fragment() {

    private val TAG = "Group_GroupList_Fragment"

    companion object {
        fun newInstance() = Groups_grouplist_fragment()
    }

    private val viewModel: GroupsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups_grouplist_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groups_recyler_list_view.layoutManager = LinearLayoutManager(activity)

        viewModel.getGroups().observe(viewLifecycleOwner, Observer<HashMap<String,Group>>{ groups ->
            groups_recyler_list_view.adapter = GroupsListAdapter(ArrayList(groups.values))
            Log.d(TAG, "Observed groups: $groups")
        })

        groups_add_group_button.setOnClickListener{
            PushData.saveGroup("Neue Gruppe", null) // TODO: let user set name and add friends
            Snackbar.make(view, "Erstelle neue Gruppe", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }



}