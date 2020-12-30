package com.example.virtualbreak.view.view_fragments.friendrequests

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.FriendListAdapter
import com.example.virtualbreak.controller.adapters.FriendRequestsAdapter
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.model.User
import kotlinx.android.synthetic.main.friend_requests_fragment.*

class FriendRequestsFragment() : Fragment() {

    lateinit var friendRequests: HashMap<String, User>
    var friendrequestsTest: ArrayList<User> = ArrayList()

    companion object {
        fun newInstance() = FriendRequestsFragment()
    }

    //evtl delete
    private lateinit var viewModel: FriendRequestsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.friend_requests_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendrequests_recyler_list_view.layoutManager = LinearLayoutManager(context)

        friendrequestsTest.add(User("a", "Freund1", "email", Status.AVAILABLE, null, null, null, null))
        friendrequestsTest.add(User("b", "Freund2", "email2", Status.AVAILABLE, null, null, null, null))

        friendrequests_recyler_list_view.adapter = FriendRequestsAdapter(friendrequestsTest)
        //TODO replace with real data from db, uncomment following:

        /*PullData.incomingFriendRequests.observe(viewLifecycleOwner, {
            //pull incoming friend requests from db
            friendRequests = it
            //pass list of users who sent you a friend request
            friendrequests_recyler_list_view.adapter = FriendRequestsAdapter(ArrayList(friendRequests.values))
        })*/
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FriendRequestsViewModel::class.java)
        // TODO: evtl delete viewmodel

    }

}