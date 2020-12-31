package com.example.virtualbreak.controller.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.model.User
import com.google.android.material.snackbar.Snackbar

class FriendRequestsAdapter(private val friendRequests: ArrayList<User>) : RecyclerView.Adapter<FriendRequestsAdapter.ViewHolderFriendRequests>() {

    lateinit var view: View

    class ViewHolderFriendRequests(itemView: View) : RecyclerView.ViewHolder(itemView){
        val acceptRequestBtn: Button
        val username_textView: TextView
        val email_textView: TextView
        val profile_imageView: ImageView
        private val TAG: String = "FriendRequestsAdapter_ViewHolder"

        init{
            acceptRequestBtn = itemView.findViewById(R.id.friendrequest_accept)
            username_textView = itemView.findViewById(R.id.friendrequest_username)
            email_textView = itemView.findViewById(R.id.friendrequest_email)
            profile_imageView = itemView.findViewById(R.id.friendrequest_img)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderFriendRequests {
        view = LayoutInflater.from(viewGroup.context).inflate(R.layout.friend_request_item, viewGroup, false)

        return ViewHolderFriendRequests(view)
    }

    override fun getItemCount(): Int {
        if (friendRequests == null)
            return 0
        else
            return friendRequests.size
    }


    override fun onBindViewHolder(holder: ViewHolderFriendRequests, position: Int) {

        holder.username_textView.text = friendRequests[position].username
        holder.email_textView.text = friendRequests[position].email

        //define click listener for viewholders view
        holder.acceptRequestBtn.setOnClickListener{

        //TODO accept friend request database logic
            Snackbar.make(view, "Add friend "+friendRequests[position].username+" uid: "+friendRequests[position].uid, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        //TODO get images from firebase storage
        //holder.profile_imageView =
    }


}