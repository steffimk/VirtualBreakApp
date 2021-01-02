package com.example.virtualbreak.view.view_fragments.groupsfriends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.FriendListAdapter
import com.example.virtualbreak.controller.adapters.groupsfriends.SearchFriendListAdapter
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_add_group.*
import kotlinx.android.synthetic.main.fragment_add_group.select_friends_recylerlist
import kotlinx.android.synthetic.main.fragment_groups_friendlist_fragment.*


class AddGroupFragment : Fragment() {

    private val TAG = "AddGroupFragment"

    private lateinit var groupsFriendsViewModel: GroupsViewModel
    lateinit var adapter: SearchFriendListAdapter
    lateinit var friends: ArrayList<User>

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
        groupsFriendsViewModel = ViewModelProvider(this).get(GroupsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //select_friends_recylerlist.setHasFixedSize(true)

        //for test
        friends = arrayListOf<User>()
        friends.add(User("a", "Freund1", "email", Status.AVAILABLE, null, false, null, null, null))
        friends.add(User("b", "Freund2", "email2", Status.BUSY, null, false, null, null, null))

        adapter = SearchFriendListAdapter(friends, context)
        //select_friends_recylerlist.adapter = SearchFriendListAdapter(friends, context)
        select_friends_recylerlist.adapter = adapter

        //get current friends from PullData and pass to recycler view adapter for friends list
        /*PullData.friends.value?.values.let{
            friends = ArrayList(PullData.friends.value?.values)
            adapter = SearchFriendListAdapter(friends, context)
        }
        select_friends_recylerlist.adapter = adapter

        //adapt friend list at changes
        PullData.friends.observe(viewLifecycleOwner, {
            select_friends_recylerlist.adapter = FriendListAdapter(ArrayList(PullData.friends.value?.values), context) // TODO: Maybe reuse old adapter
        })*/

        //Set the clicklistner to select friends and recive selected friends ids
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

        val groupName : EditText = view.findViewById(R.id.et_group_name)

        val createGroupButton: FloatingActionButton = view.findViewById(R.id.make_group_button)
        createGroupButton.setOnClickListener{
            //TODO change with freindlist, need to pull updated function, check if groupname is added, check if at least one friend is selected, go to overview again

            if (groupName.text.toString() != ""){
                if(selectFriendsIds.isNotEmpty()){
                    //TODO PushData.saveGroup(groupName.text.toString(), selectedFriendsIds), convert mutable list to array?
                    PushData.saveGroup(groupName.text.toString(), selectFriendsIds.toTypedArray())
                    view.findNavController().navigate(R.id.action_addGroupFragment_to_navHome)
                }else{
                    Toast.makeText(activity, getString(R.string.Error_no_friends), Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(activity, getString(R.string.Error_no_groupname), Toast.LENGTH_SHORT).show()
            }
        }

    }


}