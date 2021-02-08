package com.example.virtualbreak.view.view_fragments.singlegroup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.adapters.groupsfriends.SearchFriendListAdapter
import com.example.virtualbreak.controller.communication.FCMService
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.*
import com.example.virtualbreak.view.view_fragments.groupsfriends.GroupsViewModel
import kotlinx.android.synthetic.main.fragment_add_member_to_group.*


private const val GROUP_ID = "groupId"

/**
 * A simple [Fragment] subclass.
 * Use the [AddMemberToGroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddMemberToGroupFragment : Fragment() {
    private val TAG = "AddMemberToGroupFragment"

    private var groupId: String? = null

    private val groupsFriendsViewModel: GroupsViewModel by viewModels()
    lateinit var adapter: SearchFriendListAdapter

    val selectFriendsIds = ArrayList<String>()

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
        return inflater.inflate(R.layout.fragment_add_member_to_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId?.let {
            val singleGroupViewModel: SingleGroupViewModel by viewModels {
                SingleGroupViewModelFactory(
                    it
                )
            }

            adapter = SearchFriendListAdapter(ArrayList(), context)
            add_select_friends_recylerlist.adapter = adapter

            //for faster image loadig
            add_select_friends_recylerlist.setHasFixedSize(true)
            add_select_friends_recylerlist.setItemViewCacheSize(20)

            var groupName = ""
            singleGroupViewModel.getCurrentGroup()
                .observe(viewLifecycleOwner, Observer<Group?> { currentGroup ->
                    if (currentGroup != null) {
                        groupName = currentGroup.description
                    }
                })

            groupsFriendsViewModel.getFriends()
                .observe(viewLifecycleOwner, Observer<HashMap<String, User>> { friendsMap ->
                    Log.d(TAG, "Observed Friends $friendsMap")
                    if (friendsMap != null) {

                        var notAddedFriends: HashMap<String, User> = HashMap()

                        singleGroupViewModel.getGroupUsers().observe(
                            viewLifecycleOwner,
                            Observer<HashMap<String, User>> { usersMap ->
                                if (usersMap != null) {
                                    Log.i(TAG, "Users in group: " + usersMap)
                                    Log.i(TAG, "friends allg " + friendsMap)
                                    friendsMap.forEach() { (key, user) ->
                                        if (usersMap.contains(key)) {
                                            // already in list
                                        } else {
                                            notAddedFriends.put(key, user)
                                        }
                                    }
                                }
                            })


                        adapter.updateData(ArrayList(notAddedFriends.values))

                        //adapter = SearchFriendListAdapter(ArrayList(friendsMap.values), context)
                        add_select_friends_recylerlist.adapter = adapter

                        //Set the clicklistener to select friends and recive selected friends ids
                        adapter.setOnItemClickListener(object :
                            SearchFriendListAdapter.OnItemClickListener {
                            override fun onItemClick(friend: User) {
                                if (friend.isSelected && !selectFriendsIds.contains(friend.uid)) {
                                    selectFriendsIds.add(friend.uid)
                                } else {
                                    selectFriendsIds.remove(friend.uid)
                                }
                                //adapter.notifyDataSetChanged()
                                Log.d(TAG, "selectedFriends $selectFriendsIds")
                            }
                        })
                    }
                })

            exit_add_members_button.setOnClickListener {
                parentFragmentManager.setFragmentResult(
                    Constants.REQUEST_KEY_ADD_MEMBER,
                    bundleOf(Constants.BUNDLE_KEY_ADD_MEMBER to false)
                )
            }

            add_member_button.setOnClickListener {
                if (selectFriendsIds.isNotEmpty()) {
                    this.joinGroup(selectFriendsIds.toTypedArray(), groupName)
                    parentFragmentManager.setFragmentResult(
                        Constants.REQUEST_KEY_ADD_MEMBER,
                        bundleOf(Constants.BUNDLE_KEY_ADD_MEMBER to false)
                    )
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.Error_no_friends),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    private fun joinGroup(friendIds: Array<String>, groupName: String) {
        friendIds.forEach { id ->
            PushData.joinGroup(groupId!!, id)
            val title = "Neue Gruppe"
            var message = "Du wurdest zu der Gruppe \"$groupId\" hinzugefügt"
            if (groupName != "") {
                message = "Du wurdest zu der Gruppe \"$groupName\" hinzugefügt"
            }
            val recipientToken = groupsFriendsViewModel.getFriends().value?.get(id)?.fcmToken
            Log.d(TAG, "FCMToken: $recipientToken")
            if (recipientToken != null) {
                PushNotification(
                    NotificationData(title, message),
                    NotificationBody(title, message),
                    recipientToken
                ).also {
                    Log.d(TAG, "Sending notification: $it")
                    FCMService.sendNotification(it)
                }
            }
        }
    }

    companion object {
        /**
         * Creating a new instance of this fragment using the provided parameters.
         *
         * @param groupId Id the current group
         * @return A new instance of fragment AddMemberToGroupFragment.
         */
        @JvmStatic
        fun newInstance(groupId: String) =
            AddMemberToGroupFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(GROUP_ID, groupId)
                    }
                    Log.i(TAG, "created")
                }
    }
}