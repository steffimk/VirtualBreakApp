package com.example.virtualbreak.view.view_fragments.singlegroup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.groupsfriends.SearchFriendListAdapter
import com.example.virtualbreak.controller.communication.FCMService
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.NotificationBody
import com.example.virtualbreak.model.NotificationData
import com.example.virtualbreak.model.PushNotification
import com.example.virtualbreak.model.User
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

        adapter = SearchFriendListAdapter(ArrayList(), context)
        //select_friends_recylerlist.adapter = SearchFriendListAdapter(friends, context)
        add_select_friends_recylerlist.adapter = adapter

        //for faster image loadig
        add_select_friends_recylerlist.setHasFixedSize(true)
        add_select_friends_recylerlist.setItemViewCacheSize(20)

        groupsFriendsViewModel.getFriends().observe(viewLifecycleOwner, Observer<HashMap<String, User>> { friendsMap ->
            Log.d(TAG, "Observed Friends $friendsMap")
            if (friendsMap != null){

                adapter.updateData(ArrayList(friendsMap.values))
                //adapter = SearchFriendListAdapter(ArrayList(friendsMap.values), context)
                add_select_friends_recylerlist.adapter = adapter

                //Set the clicklistener to select friends and recive selected friends ids
                adapter.setOnItemClickListener(object : SearchFriendListAdapter.OnItemClickListener{
                    override fun onItemClick(friend: User) {
                        if (friend.isSelected && !selectFriendsIds.contains(friend.uid)){
                            selectFriendsIds.add(friend.uid)
                        }else{
                            selectFriendsIds.remove(friend.uid)
                        }
                        //adapter.notifyDataSetChanged()
                        Log.d(TAG, "selectedFriends $selectFriendsIds")
                    }
                })
            }
        })

        add_member_button.setOnClickListener {
            if(selectFriendsIds.isNotEmpty()){
                this.joinGroup(selectFriendsIds.toTypedArray())
                view.findNavController().navigate(R.id.action_addGroupFragment_to_navHome)
            }else{
                Toast.makeText(activity, getString(R.string.Error_no_friends), Toast.LENGTH_SHORT).show()
            }
            // TODO send add done
        }
    }

    private fun joinGroup(friendIds: Array<String>) {
        friendIds.forEach { id ->
            PushData.joinGroup(groupId!!, id)
            val title = "Neue Gruppe"
            //val message = "Du wurdest zu der Gruppe \"$groupName\" hinzugefügt"
            val message = "Du wurdest zu der Gruppe \"$groupId\" hinzugefügt"
            val recipientToken = groupsFriendsViewModel.getFriends().value?.get(id)?.fcmToken
            Log.d(TAG, "FCMToken: $recipientToken")
            if(recipientToken != null) {
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
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment addMemberToGroupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(groupId : String) =
            AddMemberToGroupFragment()
                .apply {
                arguments = Bundle().apply {
                    putString(GROUP_ID, groupId)
                }
            }
    }
}