package com.example.virtualbreak.view.view_fragments.singlegroup

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.SingleGroupMembersAdapter
import kotlinx.android.synthetic.main.fragment_bored_api.*
import kotlinx.android.synthetic.main.fragment_single_group_members.*

private const val GROUP_ID = "groupId"

/**
 * Fragment shows users that are part of this group in a horizontal swipe view
 */
class SingleGroupMembersFragment : Fragment() {
    private var groupId: String? = null
    val TAG = "SingleGroupMembersFragment"
    var memberListAdapter: SingleGroupMembersAdapter? = null
    var singleGroupFragment: SingleGroupFragment? = null // reference to parent fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getString(GROUP_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_single_group_members, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupId?.let{

            val singleGroupViewModel: SingleGroupViewModel by viewModels { SingleGroupViewModelFactory(
                it
            ) }

            singleGroupViewModel.getCurrentGroup()
            singleGroupViewModel.getGroupUsers() // to trigger start init by lazy

            singlegroup_members_recyclerlistview.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            //for faster image loadig
            singlegroup_members_recyclerlistview.setHasFixedSize(true)
            singlegroup_members_recyclerlistview.setItemViewCacheSize(20)


            singleGroupViewModel.getGroupUsers().observe(viewLifecycleOwner, { members ->

                Log.d(TAG, "Observed friends: $members")
                if(!members.isEmpty() && members != null){
                    singleGroupFragment?.let {
                        memberListAdapter = SingleGroupMembersAdapter(
                            ArrayList(
                                members.values
                            ), context, it
                        )
                    }
                    singlegroup_members_recyclerlistview.adapter = memberListAdapter

                    //set text how many users are members of this group
                    singelgroup_members_header.text = ""+members.size+" Mitglieder:"
                }
            })

            expand_singlegroup_members_btn.setOnClickListener {
                toggleGroupMembersVisibility()
            }
            expand_singlegroup_members_relative_layout.setOnClickListener {
                toggleGroupMembersVisibility()
            }


        }
    }

    private fun toggleGroupMembersVisibility() {
        if (singlegroup_members_recyclerlistview.getVisibility() === View.VISIBLE) {

            // The transition of the hiddenView is carried out
            //  by the TransitionManager class.
            // Here we use an object of the AutoTransition
            // Class to create a default transition.
            TransitionManager.beginDelayedTransition(
                singlegroup_members_base_cardview,
                AutoTransition()
            )
            singlegroup_members_recyclerlistview.setVisibility(View.GONE)
            expand_singlegroup_members_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
        } else {
            TransitionManager.beginDelayedTransition(
                singlegroup_members_base_cardview,
                AutoTransition()
            )
            singlegroup_members_recyclerlistview.setVisibility(View.VISIBLE)
            expand_singlegroup_members_btn.setImageResource(R.drawable.ic_baseline_expand_less_24)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment SingleGroupMembersFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, singleGroupFragment: SingleGroupFragment) =
            SingleGroupMembersFragment().apply {
                arguments = Bundle().apply {
                    putString(GROUP_ID, param1)
                }
                this.singleGroupFragment = singleGroupFragment
            }
    }
}