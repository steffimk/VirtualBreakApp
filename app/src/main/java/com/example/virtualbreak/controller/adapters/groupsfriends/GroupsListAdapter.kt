package com.example.virtualbreak.controller.adapters.groupsfriends

import android.util.Log
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Group


/**
 * This Adapter manages the Receyler View of the Groups in the groups_grouplist_fragment
 */
class GroupsListAdapter : RecyclerView.Adapter<GroupsListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val  textView: TextView
        private val TAG: String = "GroupsListAdapter_ViewHolder"

        init{
            textView = itemView.findViewById(R.id.group_list_name)

            //define click listener for viewholders view
            itemView.setOnClickListener{
                var position: Int = adapterPosition
                var context = itemView.context
                val prefs = context.getSharedPreferences("com.example.virtualbreak", Context.MODE_PRIVATE)
                // TODO: potentially not working correctly if new group was added and positions in PullData.groups changed
                // Possible solution: Better to use ids instead of position -> save ids in items like SingleGroupRoom
                val groupId = ArrayList(PullData.groups.keys)[position]
                prefs.edit().putString("com.example.virtualbreak.groupId", groupId).apply()
                Log.d(TAG, "GroupId $groupId added to shared preferences")
                itemView.findNavController().navigate(R.id.action_nav_home_to_singleGroupFragment)
            }

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.group_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //holder.textView.text = allGroups[position].description
        // Get pulled groups, transform HashMap in ArrayList, get group description
        holder.textView.text = ArrayList(PullData.groups.values)[position].description
    }

    override fun getItemCount(): Int {
        return PullData.groups.size
    }


















}