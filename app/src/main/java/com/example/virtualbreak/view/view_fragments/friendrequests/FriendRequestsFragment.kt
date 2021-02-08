package com.example.virtualbreak.view.view_fragments.friendrequests

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
import com.example.virtualbreak.controller.adapters.FriendRequestsAdapter
import com.example.virtualbreak.controller.adapters.FriendRequestsOutgoingAdapter
import com.example.virtualbreak.model.User
import kotlinx.android.synthetic.main.fragment_friend_requests.*

class FriendRequestsFragment() : Fragment() {

    private val TAG = "FriendRequestsFragment"

    companion object {
        fun newInstance() = FriendRequestsFragment()
    }

    private val viewModel: FriendRequestsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_friend_requests, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendrequests_recyler_list_view.layoutManager = LinearLayoutManager(context)
        outgoing_friendrequests_recyler_list_view.layoutManager = LinearLayoutManager(context)

        viewModel.getIncomingFriendRequests().observe(viewLifecycleOwner, Observer<HashMap<String,User>>{ incomingFriendRequests ->
            Log.d(TAG, "Observed the following incomingFriendRequests: $incomingFriendRequests")

            if(incomingFriendRequests.size == 0){
                text_no_incoming_friendrequests.visibility = View.VISIBLE
            } else{
                text_no_incoming_friendrequests.visibility = View.GONE
            }
            //pass list of users who sent you a friend request
            friendrequests_recyler_list_view.adapter =
                FriendRequestsAdapter(ArrayList(incomingFriendRequests.values))
        })

        viewModel.getOutgoingFriendRequests().observe(viewLifecycleOwner, Observer<HashMap<String,User>>{ outgoingFriendRequests ->
            Log.d(TAG, "Observed the following incomingFriendRequests: $outgoingFriendRequests")

            if(outgoingFriendRequests.size == 0){
                text_no_outgoing_friendrequests.visibility = View.VISIBLE
            } else{
                text_no_outgoing_friendrequests.visibility = View.GONE
            }
            //pass list of users who sent you a friend request
            outgoing_friendrequests_recyler_list_view.adapter =
                FriendRequestsOutgoingAdapter(ArrayList(outgoingFriendRequests.values))
        })


        friendrequests_search_friends_btn.setOnClickListener{
            view.findNavController().navigate(R.id.action_friendrequests_to_addfriends)
        }
    }

}