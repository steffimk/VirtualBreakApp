package com.example.virtualbreak.controller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import kotlinx.android.synthetic.main.group_list_item.*

class GroupsListAdapter : RecyclerView.Adapter<GroupsListAdapter.ViewHolder>() {


    //TODO fetch data from firebase
    private val testNames = arrayOf("Arbeit", "Uni","Sport")


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val  textView: TextView

        init{
            textView = itemView.findViewById(R.id.group_list_name)

            //define click listener for viewholders view
            itemView.setOnClickListener{
                var position: Int = adapterPosition
                var context = itemView.context

                //TODO GO TO selectet GROUP - pass group data
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
    }

    override fun getItemCount(): Int {
        //TODO size = dataSet.size
        return testNames.size
    }
















}