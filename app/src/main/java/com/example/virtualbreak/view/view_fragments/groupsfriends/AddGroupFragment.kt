package com.example.virtualbreak.view.view_fragments.groupsfriends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.transition.Slide
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.groupsfriends.SearchFriendListAdapter
import com.example.virtualbreak.controller.communication.FCMService
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_add_group.select_friends_recylerlist


class AddGroupFragment : Fragment() {

    private val TAG = "AddGroupFragment"

    private val groupsFriendsViewModel: GroupsViewModel by viewModels()
    lateinit var adapter: SearchFriendListAdapter

    val selectFriendsIds = ArrayList<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_group, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //select_friends_recylerlist.setHasFixedSize(true)

        addTransition()

        adapter = SearchFriendListAdapter(ArrayList(), context)
        //select_friends_recylerlist.adapter = SearchFriendListAdapter(friends, context)
        select_friends_recylerlist.adapter = adapter

        //for faster image loadig
        select_friends_recylerlist.setHasFixedSize(true)
        select_friends_recylerlist.setItemViewCacheSize(20)


        groupsFriendsViewModel.getFriends().observe(viewLifecycleOwner, Observer<HashMap<String,User>> { friendsMap ->
            Log.d(TAG, "Observed Friends $friendsMap")
            if (friendsMap != null){

                adapter.updateData(ArrayList(friendsMap.values))
                //adapter = SearchFriendListAdapter(ArrayList(friendsMap.values), context)
                select_friends_recylerlist.adapter = adapter

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

        val groupName : EditText = view.findViewById(R.id.et_group_name)

        val createGroupButton: FloatingActionButton = view.findViewById(R.id.make_group_button)
        createGroupButton.setOnClickListener{
            //TODO change with freindlist, need to pull updated function, check if groupname is added, check if at least one friend is selected, go to overview again

            if (groupName.text.toString() != ""){
                if(selectFriendsIds.isNotEmpty()){
                    //TODO PushData.saveGroup(groupName.text.toString(), selectedFriendsIds), convert mutable list to array?
                    this.createNewGroup(groupName.text.toString(), selectFriendsIds.toTypedArray())
                    view.findNavController().navigate(R.id.action_addGroupFragment_to_navHome)
                }else{
                    Toast.makeText(activity, getString(R.string.Error_no_friends), Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(activity, getString(R.string.Error_no_groupname), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun createNewGroup(groupName: String, friendIds: Array<String>) {
        PushData.saveGroup(groupName, friendIds)
        friendIds.forEach { id ->
            val title = "Neue Gruppe"
            val message = "Du wurdest zu der Gruppe \"$groupName\" hinzugefügt"
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

    private fun addTransition() {
        returnTransition = Slide().apply {
            duration = resources.getInteger(R.integer.motion_duration_small).toLong()
            addTarget(R.id.groups_recyler_list_view)
        }

    }

}