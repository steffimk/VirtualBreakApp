package com.example.virtualbreak.controller.adapters.groupsfriends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PullData

class SearchFriendListAdapter: RecyclerView.Adapter<SearchFriendListAdapter.ViewHolderFriends>() {

    //TODO fetch freindlist from firebase, map to arraylist?
    private val testNames = arrayOf("Friend1", "Friend2", "Friend3")
    //Hashmapp <Userid, User>
    private val friendsList = PullData.friends

    var tracker: SelectionTracker<String>? = null

    init{
        setHasStableIds(true)
    }


    inner class ViewHolderFriends(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.friend_list_name)

        init {
            //define click listener for viewholders view
            itemView.setOnClickListener {
                var position: Int = adapterPosition
                var context = itemView.context
                //TODO Got to Friend profile?
                //Do Nothing
            }
        }

        fun bind(value: Int, isActivated: Boolean = false){
            textView.text = value.toString()
            itemView.isActivated = isActivated
        }

        fun getItemDetails() {
            /*
            : ItemDetailsLookup.ItemDetails<String>
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = adapterPosition
                //override fun getSelectionKey(): String? = itemId
            }

             */
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderFriends {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.friend_list_item, viewGroup, false)

        return ViewHolderFriends(view)
    }

    override fun getItemCount(): Int {
        //TODO size = dataSet.size
        return testNames.size
    }


    override fun onBindViewHolder(holder: ViewHolderFriends, position: Int) {
        //TODO get groupnames form content at position, replace contents in view with new
        //holder.textView.text = testNames[position]
        //TODO arraylist to get positions???
        val friend = friendsList[position]
        /*
        tracker?.let {
            holder.bind(friend, it.isSelected(friend?.uid))
        }
        */
    }

/*
    override fun getItemId(position: Int): Long {
        //TODO get UserId to get key
    }
*/



}
