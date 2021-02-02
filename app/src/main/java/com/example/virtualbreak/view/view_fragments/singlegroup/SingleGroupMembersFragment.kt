package com.example.virtualbreak.view.view_fragments.singlegroup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.SingleGroupMembersAdapter
import kotlinx.android.synthetic.main.fragment_single_group_members.*

private const val GROUP_ID = "groupId"

/**
 * A simple [Fragment] subclass.
 * Use the [SingleGroupMembersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SingleGroupMembersFragment : Fragment() {
    private var groupId: String? = null
    val TAG = "SingleGroupMembersFragment"
    var memberListAdapter: SingleGroupMembersAdapter? = null

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
                memberListAdapter = SingleGroupMembersAdapter(
                    ArrayList(
                        members.values
                    ), context
                )
                singlegroup_members_recyclerlistview.adapter = memberListAdapter
                //don't reuse old adapter
                /*if(memberListAdapter== null){
                    memberListAdapter = SingleGroupMembersAdapter(
                        ArrayList(
                            members.values
                        ), context
                    )
                    singlegroup_members_recyclerlistview.adapter = memberListAdapter
                }
                else{ //update adapter with new data, if already exists
                    memberListAdapter?.updateData(ArrayList(members.values))
                }*/

                Log.d(TAG, "Observed friends: $members")
            })


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
        fun newInstance(param1: String) =
            SingleGroupMembersFragment().apply {
                arguments = Bundle().apply {
                    putString(GROUP_ID, param1)
                }
            }
    }
}