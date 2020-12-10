package com.example.virtualbreak.controller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R

class FriendListAdapter : RecyclerView.Adapter<FriendListAdapter.ViewHolderFriends>() {

    //TODO fetch data from firebase
    private val testNames = arrayOf("Friend1", "Friend2","Friend3")


    class ViewHolderFriends(itemView: View) : RecyclerView.ViewHolder(itemView){
        val  textView: TextView

        init{
            textView = itemView.findViewById(R.id.friend_list_name)

            //define click listener for viewholders view
            itemView.setOnClickListener{
                var position: Int = adapterPosition
                var context = itemView.context
                //TODO GO TO selectet GROUP
            }


        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderFriends {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.friend_list_item, viewGroup, false)

        return ViewHolderFriends(view)
    }

    override fun getItemCount(): Int {
        //TODO size = dataSet.size
        return testNames.size
    }


    override fun onBindViewHolder(holder: ViewHolderFriends, position: Int) {
        //TODO get groupnames form content at position, replace contents in view with new
        holder.textView.text = testNames[position]
    }


}