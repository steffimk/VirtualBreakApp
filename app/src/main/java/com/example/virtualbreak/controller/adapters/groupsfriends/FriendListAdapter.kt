package com.example.virtualbreak.controller.adapters.groupsfriends

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Group
import com.example.virtualbreak.model.User
import com.makeramen.roundedimageview.RoundedImageView


/**
 * This Adapter manages the content of the Friendlist in the groups_friendlist_fragment
 */
class FriendListAdapter : RecyclerView.Adapter<FriendListAdapter.ViewHolderFriends>() {

    //TODO fetch data from firebase
    private val testNames = arrayOf("Friend1", "Friend2","Friend3")
    var allFriends = arrayListOf<User>()


    class ViewHolderFriends(itemView: View) : RecyclerView.ViewHolder(itemView){
        val  textView: TextView
        val profilPicture: RoundedImageView

        init{
            textView = itemView.findViewById(R.id.friend_list_name)
            profilPicture = itemView.findViewById<RoundedImageView>(R.id.friend_list_image)

            //define click listener for viewholders view
            itemView.setOnClickListener{
                var position: Int = adapterPosition
                var context = itemView.context
                //TODO Got to Friend profile?
                //Do Nothing
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
        //return allFriends.size
    }


    override fun onBindViewHolder(holder: ViewHolderFriends, position: Int) {
        //TODO get groupnames form content at position, replace contents in view with new
        holder.textView.text = testNames[position]
        getFriends()
        //holder.textView.text = allFriends[position].username
        //TODO holder.profilPicture.setImageResource()
        // Set status
    }

    fun getFriends(){
        Log.d("Groups","friends" + PullData.friends)
        PullData.friends.forEach {
                (key, friend) -> allFriends.add(friend)
        }
        Log.d("Groups","allFriends" + allFriends)

    }


}