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
import com.example.virtualbreak.model.User
import kotlinx.android.synthetic.main.fragment_groups_friendlist_fragment.*


class Groups_friendlist_fragment : Fragment() {

    companion object {
        fun newInstance() = Groups_friendlist_fragment()
    }

    private val TAG = "Groups_Friendlist_Fragment"

    private val viewModel: GroupsViewModel by viewModels()

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

         //for test
        /*var friends: ArrayList<User> = ArrayList()
        friends.add(User("a", "Freund1", "email", Status.AVAILABLE, null, null, null, null))
        friends.add(User("b", "Freund2", "email2", Status.BUSY, null, null, null, null))
        friends_recyler_list_view.adapter = FriendListAdapter(friends, context)*/

        viewModel.getFriends().observe(viewLifecycleOwner, Observer<HashMap<String,User>>{ friends ->
            friends_recyler_list_view.adapter = FriendListAdapter(ArrayList(friends.values), context) // TODO: Maybe reuse old adapter
            Log.d(TAG, "Observed friends: $friends")
        })

        friends_add_friends_button.setOnClickListener{
            //navigate to add friends fragment
            view.findNavController().navigate(R.id.action_nav_home_to_addFriendsFragment)

        }
    }


}