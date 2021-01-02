package com.example.virtualbreak.controller.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.User
import com.makeramen.roundedimageview.RoundedImageView
import com.example.virtualbreak.model.Status


/**
 * This Adapter manages the content of the Friendlist in the groups_friendlist_fragment
 */

class FriendListAdapter(private val friends: ArrayList<User>, private val context: Context?) : RecyclerView.Adapter<FriendListAdapter.ViewHolderFriends>() {

    lateinit var view: View
    val TAG = "FriendListAdapter"

    class ViewHolderFriends(itemView: View) : RecyclerView.ViewHolder(itemView){
        val  textView: TextView
        val statusCircleImg: ImageView
        private val TAG: String = "FriendListAdapter_ViewHolder"

        init{
            textView = itemView.findViewById(R.id.friend_list_name)
            statusCircleImg = itemView.findViewById(R.id.status_circle_img)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderFriends {
        view = LayoutInflater.from(viewGroup.context).inflate(R.layout.friend_list_item, viewGroup, false)

        return ViewHolderFriends(view)
    }

    override fun getItemCount(): Int {
        if (friends == null)
            return 0
        else
            return friends.size
    }


    override fun onBindViewHolder(holder: ViewHolderFriends, position: Int) {
        //holder.textView.text = ArrayList(PullData.friends.value?.values)[position].username
        holder.textView.text = friends[position].username

        context?.let{
            when(friends[position].status){
                Status.AVAILABLE -> holder.statusCircleImg.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_available))
                Status.BUSY -> holder.statusCircleImg.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_busy))
                Status.STUDYING -> holder.statusCircleImg.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_studying))
                else -> holder.statusCircleImg.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_unknown))
            }
        }

        view.setOnClickListener {
            val friendId = friends[position].uid
            //evtl go to selected friend (evtl no need, if status shown in list) or show popup
            Log.d(TAG, "FriendId $friendId was clicked on")
        }

    }


}