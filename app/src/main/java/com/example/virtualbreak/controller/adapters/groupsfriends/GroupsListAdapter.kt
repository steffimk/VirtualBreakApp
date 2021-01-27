package com.example.virtualbreak.controller.adapters.groupsfriends

import android.util.Log
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Group
import com.example.virtualbreak.view.view_fragments.groupsfriends.GroupsFriendsFragmentDirections
import kotlinx.android.synthetic.main.group_list_item.*


/**
 * This Adapter manages the Receyler View of the Groups in the groups_grouplist_fragment
 */
class GroupsListAdapter(val groups: ArrayList<Group>) : RecyclerView.Adapter<GroupsListAdapter.ViewHolder>() {

    lateinit var view: View
    val TAG = "GroupsListAdapter"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val  textView: TextView
        val groupImg: ImageView
        private val TAG: String = "GroupsListAdapter_ViewHolder"

        init{
            textView = itemView.findViewById(R.id.group_list_name)
            groupImg = itemView.findViewById(R.id.group_list_img)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(viewGroup.context).inflate(R.layout.group_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get pulled groups, transform HashMap in ArrayList, get group description
        holder.textView.text = groups[position].description

        //TODO choose image for group
        //holder.groupImg = ...

        view.setOnClickListener{
            val group = groups[position]
            val groupId = group.uid
            //SharedPrefManager.instance.saveGroupId(groupId)
            Log.d(TAG, "clicked on group "+groupId)
            val action = GroupsFriendsFragmentDirections.actionNavHomeToSingleGroupFragment(groupId)
            Navigation.findNavController(view).navigate(action)
        }

    }

    override fun getItemCount(): Int {
        if (groups == null)
            return 0
        else
            return groups.size
    }
















}