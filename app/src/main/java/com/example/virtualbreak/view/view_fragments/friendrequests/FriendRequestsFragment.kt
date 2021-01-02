package com.example.virtualbreak.view.view_fragments.friendrequests

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.FriendRequestsAdapter
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.model.User
import kotlinx.android.synthetic.main.friend_requests_fragment.*

class FriendRequestsFragment() : Fragment() {

    private val TAG = "FriendRequestsFragment"

//    var friendrequestsTest: ArrayList<User> = ArrayList()

    companion object {
        fun newInstance() = FriendRequestsFragment()
    }

    private val viewModel: FriendRequestsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.friend_requests_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendrequests_recyler_list_view.layoutManager = LinearLayoutManager(context)

        /*friendrequestsTest.add(User("a", "Freund1", "email", Status.AVAILABLE, null, null, null, null))
        friendrequestsTest.add(User("b", "Freund2", "email2", Status.AVAILABLE, null, null, null, null))

        friendrequests_recyler_list_view.adapter = FriendRequestsAdapter(friendrequestsTest)*/

        viewModel.getIncomingFriendRequests().observe(viewLifecycleOwner, Observer<HashMap<String,User>>{ incomingFriendRequests ->
            Log.d(TAG, "Observed the following incomingFriendRequests: $incomingFriendRequests")
            //pass list of users who sent you a friend request
            friendrequests_recyler_list_view.adapter =
                FriendRequestsAdapter(ArrayList(incomingFriendRequests.values))
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}