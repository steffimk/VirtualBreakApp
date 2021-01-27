package com.example.virtualbreak.view.view_fragments.singlegroup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.GridView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.adapters.SingleGroupRoomsAdapter
import com.example.virtualbreak.controller.communication.FCMService
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.*
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.Roomtype
import com.example.virtualbreak.model.User
import com.example.virtualbreak.view.view_activitys.NavigationDrawerActivity
import com.example.virtualbreak.view.view_activitys.VideoCallActivity
import com.example.virtualbreak.view.view_activitys.breakroom.BreakRoomActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nambimobile.widgets.efab.FabOption
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_groups_friendlist_fragment.*
import kotlinx.android.synthetic.main.fragment_singlegroup.*

class SingleGroupFragment : Fragment() {

    private val TAG: String = "SingleGroupFragment"

    //Navigation argument to pass selected group from GroupsFriendsFragment (GroupsListAdapter) to SingleGroupFragment
    val args: SingleGroupFragmentArgs by navArgs()
    private lateinit var groupId: String

    private val singleGroupViewModel: SingleGroupViewModel by viewModels {
        SingleGroupViewModelFactory(
            args.groupId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_singlegroup, container, false)

        setHasOptionsMenu(true)

        groupId = args.groupId

        singleGroupViewModel.getGroupUsers() // to trigger start init by lazy

        Log.d(TAG, "Gruppe DESCRIPTION \t" + singleGroupViewModel.getCurrentGroup().value?.description)

        singleGroupViewModel.getCurrentGroup()
            .observe(viewLifecycleOwner, Observer<Group?> { currentGroup ->
                if (currentGroup != null) {
                    activity?.toolbar?.title = currentGroup.description
                    Log.d(TAG, "Found group name " + currentGroup.description)
                }
            })

        //instantiate member list fragment and room grid fragment
        activity?.supportFragmentManager?.beginTransaction()?.let {
            it.replace(
                R.id.singlegroup_containerview_members,
                SingleGroupMembersFragment.newInstance(groupId)
            )
            //it.addToBackStack(null)
            it.commit()
        }

        // display rooms
        activity?.supportFragmentManager?.beginTransaction()?.let {
            it.replace(
                R.id.singlegroup_containerview_rooms,
                SingleGroupRoomsFragment.newInstance(groupId)
            )
            //it.addToBackStack(null)
            it.commit()
        }

/*
        root.findViewById<MaterialButtonToggleGroup>(R.id.singlegroup_toggleButton).addOnButtonCheckedListener {
                group, checkedId, isChecked ->

                when(checkedId){
                    R.id.singlegroup_toggle_room ->
                        activity?.supportFragmentManager?.beginTransaction()?.let {
                            it.replace(R.id.singlegroup_fragment_containerview, SingleGroupRoomsFragment.newInstance(groupId))
                            //it.addToBackStack(null)
                            it.commit()
                        }
                    R.id.singlegroup_toggle_members ->
                        activity?.supportFragmentManager?.beginTransaction()?.let {
                            it.replace(R.id.singlegroup_fragment_containerview, SingleGroupMembersFragment.newInstance(groupId))
                            //it.addToBackStack(null)
                            it.commit()
                        }
                }


        }*/
        var userName: String? = SharedPrefManager.instance.getUserName()


        return root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.singlegroup_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {

        R.id.action_leave_group -> {
            singleGroupViewModel.getCurrentGroup().value?.let { PushData.leaveGroup(it) } //TODO here bug, currentgroup can be null
            view?.findNavController()?.navigate(R.id.action_singleGroupFragment_to_home)
            true
        }
        //TODO edit group name? maybe in extra fragment
        else -> {
            super.onOptionsItemSelected(item)
        }
    }


}