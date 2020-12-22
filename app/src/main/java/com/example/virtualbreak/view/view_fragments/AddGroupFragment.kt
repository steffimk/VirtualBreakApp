package com.example.virtualbreak.view.view_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.groupsfriends.GroupsListAdapter
import com.example.virtualbreak.controller.adapters.groupsfriends.MyItemDetailsLookup
import com.example.virtualbreak.controller.adapters.groupsfriends.SearchFriendListAdapter
import kotlinx.android.synthetic.main.fragment_add_group.*
import kotlinx.android.synthetic.main.fragment_groups_grouplist_fragment.*


class AddGroupFragment : Fragment() {

    private val adapter = SearchFriendListAdapter()
    private var tracker: SelectionTracker<String>? = null

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
        select_friends_recylerlist.adapter = adapter
        adapter.notifyDataSetChanged()

        /*
        tracker = SelectionTracker.Builder<String>(
            "mySelection",
            select_friends_recylerlist,
            StableIdKeyProvider(select_friends_recylerlist),
            MyItemDetailsLookup(select_friends_recylerlist),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker
*/
    }

}