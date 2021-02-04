package com.example.virtualbreak.view.view_fragments.groupsfriends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.FriendListAdapter
import com.example.virtualbreak.controller.adapters.SingleGroupMembersAdapter
import com.example.virtualbreak.model.User
import kotlinx.android.synthetic.main.fragment_groups_friendlist_fragment.*

/**
 * Friend list fragment in main fragment
 */
class Groups_friendlist_fragment : Fragment() {

    companion object {
        fun newInstance() = Groups_friendlist_fragment()
    }

    private val TAG = "Groups_Friendlist_Fragment"

    private val viewModel: GroupsViewModel by viewModels()

    var friendsListAdapter: FriendListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups_friendlist_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friends_recyler_list_view.layoutManager = LinearLayoutManager(activity)

        //for faster image loadig
        friends_recyler_list_view.setHasFixedSize(true)
        friends_recyler_list_view.setItemViewCacheSize(20)

        viewModel.getFriends().observe(viewLifecycleOwner, Observer<HashMap<String,User>>{ friends ->
            friendsListAdapter = FriendListAdapter(ArrayList(friends.values), context)
            friends_recyler_list_view.adapter = friendsListAdapter

            //don't reuse old adapter
            /*if(friendsListAdapter == null){
                friendsListAdapter = FriendListAdapter(ArrayList(friends.values), context)
                friends_recyler_list_view.adapter = friendsListAdapter
            }
            else{ //update old adapter with new data
                friendsListAdapter?.updateData(ArrayList(friends.values))
            }*/

            Log.d(TAG, "Observed friends: $friends")
        })

        friends_add_friends_button.setOnClickListener{
            //navigate to add friends fragment
            view.findNavController().navigate(R.id.action_nav_home_to_addFriendsFragment)

        }
    }


}