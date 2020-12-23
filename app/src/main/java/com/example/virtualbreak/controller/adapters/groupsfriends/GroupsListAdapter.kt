package com.example.virtualbreak.controller.adapters.groupsfriends

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


    //TODO fetch data from firebase
    private val testNames = arrayOf("Arbeit", "Uni","Sport")
    var allGroups = arrayListOf<Group>()

    fun getGroups(){
        PullData.groups.forEach {
                (key, group) -> allGroups.add(group)
        }

    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val  textView: TextView

        init{
            textView = itemView.findViewById(R.id.group_list_name)

            //define click listener for viewholders view
            itemView.setOnClickListener{
                var position: Int = adapterPosition
                var context = itemView.context

                //TODO  pass group data? Save current group?
                //Go to the selected Group
                itemView.findNavController().navigate(R.id.action_nav_home_to_singleGroupFragment)
            }


        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.group_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //TODO get groupnames form content at position, replace contents in view with new
        holder.textView.text = testNames[position]
        //allGroups.forEach { (key, value) -> println("$key = $value") }
        //holder.textView.text = allGroups[position].description
    }

    override fun getItemCount(): Int {
        //TODO size = dataSet.size
        return testNames.size
    }
















}