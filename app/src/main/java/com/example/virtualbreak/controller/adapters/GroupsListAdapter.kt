package com.example.virtualbreak.controller.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Group
import kotlinx.android.synthetic.main.group_list_item.*

class GroupsListAdapter(val groups: ArrayList<Group>) : RecyclerView.Adapter<GroupsListAdapter.ViewHolder>() {

    lateinit var view: View
    val TAG = "GroupsListAdapter"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val  textView: TextView
        private val TAG: String = "GroupsListAdapter_ViewHolder"

        init{
            textView = itemView.findViewById(R.id.group_list_name)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(viewGroup.context).inflate(R.layout.group_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get pulled groups, transform HashMap in ArrayList, get group description
        holder.textView.text = groups[position].description

        view.setOnClickListener{
            val groupId = groups[position].uid
            SharedPrefManager.instance.saveGroupId(groupId)
            Log.d(TAG, "clicked on group "+groupId)
            view.findNavController().navigate(R.id.action_nav_home_to_singleGroupFragment)
        }

    }

    override fun getItemCount(): Int {
        if (groups == null)
            return 0
        else
            return groups.size
    }
















}