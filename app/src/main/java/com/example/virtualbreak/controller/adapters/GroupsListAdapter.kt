package com.example.virtualbreak.controller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import kotlinx.android.synthetic.main.group_list_item.*

class GroupsListAdapter : RecyclerView.Adapter<GroupsListAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val  textView: TextView

        init{
            textView = view.findViewById(R.id.group_list_name)
            //define click listener for viewholders view?
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.group_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //TODO get groupnames form content at position, replace contents in view with new
        //holder.textView.text = dataSet[position]
    }

    override fun getItemCount(): Int {
        //TODO size = dataSet.size
        return 1
    }















}