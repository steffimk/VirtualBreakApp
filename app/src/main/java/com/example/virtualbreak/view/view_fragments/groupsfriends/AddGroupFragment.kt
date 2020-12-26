package com.example.virtualbreak.view.view_fragments.groupsfriends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.groupsfriends.GroupsListAdapter
import com.example.virtualbreak.controller.adapters.groupsfriends.MyItemDetailsLookup
import com.example.virtualbreak.controller.adapters.groupsfriends.SearchFriendListAdapter
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Friend
import com.example.virtualbreak.view.view_fragments.singlegroup.SingleGroupViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_add_group.*
import kotlinx.android.synthetic.main.fragment_groups_grouplist_fragment.*


class AddGroupFragment : Fragment() {

    private lateinit var groupsFriendsViewModel: GroupsViewModel


    private val adapter: SearchFriendListAdapter = SearchFriendListAdapter()
    val selectFriendsIds = mutableListOf<String>()
    //private var tracker: SelectionTracker<String>? = null


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
        select_friends_recylerlist.adapter = adapter
        adapter.testFriends()
        adapter.getFriends()
        //adapter.notifyDataSetChanged()

        adapter!!.setOnItemClickListener(object : SearchFriendListAdapter.OnItemClickListener{
            override fun onItemClick(friend: Friend) {
                if (friend.isSelectet){
                    selectFriendsIds.add(friend.uid)
                }else{
                    selectFriendsIds.remove(friend.uid)
                }
                //adapter.notifyDataSetChanged()
                Log.d("Groups", "selectedFriends $selectFriendsIds")
            }
        })

        val groupName : EditText = view.findViewById(R.id.et_group_name)

        val createGroupButton: FloatingActionButton = view.findViewById(R.id.make_group_button)
        createGroupButton.setOnClickListener{
            //TODO change with freindlist, need to pull updated function, check if groupname is added, check if at least one friend is selected, go to overview again
            //Save the created Group with freindlist selectedfriends, convert friends to Users again?
            //PushData.saveGroup(groupName.text.toString(), selectedFriendsIds)
        }

    }


}