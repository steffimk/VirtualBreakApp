package com.example.virtualbreak.controller.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PullData

class FriendListAdapter : RecyclerView.Adapter<FriendListAdapter.ViewHolderFriends>() {

    private val testNames = arrayOf("Friend1", "Friend2","Friend3")


    class ViewHolderFriends(itemView: View) : RecyclerView.ViewHolder(itemView){
        val  textView: TextView
        private val TAG: String = "FriendListAdapter_ViewHolder"

        init{
            textView = itemView.findViewById(R.id.friend_list_name)

            //define click listener for viewholders view
            itemView.setOnClickListener{
                var position: Int = adapterPosition
                var context = itemView.context
                val prefs = context.getSharedPreferences("com.example.virtualbreak", Context.MODE_PRIVATE)
                // TODO: potentially not working correctly if new friend was added and positions in PullData.friends changed
                // Possible solution: Better to use ids instead of position -> save ids in items like SingleGroupRoom
                val friendId = ArrayList(PullData.friends.keys)[position]
                prefs.edit().putString("com.example.virtualbreak.friendId", friendId).apply()
                Log.d(TAG, "FriendId $friendId added to shared preferences")
                //TODO GO TO selected Friend
            }


        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderFriends {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.friend_list_item, viewGroup, false)

        return ViewHolderFriends(view)
    }

    override fun getItemCount(): Int {
        return PullData.friends.size
    }


    override fun onBindViewHolder(holder: ViewHolderFriends, position: Int) {
        holder.textView.text = ArrayList(PullData.friends.values)[position].username
    }


}