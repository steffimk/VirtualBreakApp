package com.example.virtualbreak.controller.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.User
import com.example.virtualbreak.view.view_fragments.friendrequests.FriendRequestsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

/**
 * This Adapter manages the list of outgoing Friend requests in FriendRequestsFragment
 */
class FriendRequestsOutgoingAdapter(private val friendRequests: ArrayList<User>): RecyclerView.Adapter<FriendRequestsOutgoingAdapter.ViewHolderFriendRequests>() {

    lateinit var view: View
    val TAG = "FriendRequestsOutgoingAdapter"

    class ViewHolderFriendRequests(itemView: View) : RecyclerView.ViewHolder(itemView){
        val deleteRequestBtn: Button
        val username_textView: TextView
        val email_textView: TextView
        val profile_imageView: ImageView
        private val TAG: String = "FriendRequestsAdapter_ViewHolder"

        init{
            deleteRequestBtn = itemView.findViewById(R.id.friendrequest_outgoing_delete)
            username_textView = itemView.findViewById(R.id.friendrequest_outgoing_username)
            email_textView = itemView.findViewById(R.id.friendrequest_outgoing_email)
            profile_imageView = itemView.findViewById(R.id.friendrequest_outgoing_img)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderFriendRequests {
        view = LayoutInflater.from(viewGroup.context).inflate(R.layout.friend_request_outgoing_item, viewGroup, false)

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

        loadProfilePicture(holder, friendRequests[position].uid)

        //define click listener for viewholders view
        holder.deleteRequestBtn.setOnClickListener{

            PushData.removeFriendRequest(friendRequests[position].uid)
            Snackbar.make(view, "Du hast die Freundschaftsanfrage an ${friendRequests[position].username} r??ckg??ngig gemacht", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun loadProfilePicture(holder: FriendRequestsOutgoingAdapter.ViewHolderFriendRequests, userId: String) {

        val mStorageRef = FirebaseStorage.getInstance().getReference()
        mStorageRef.child("img/profilePics/$userId").downloadUrl
            .addOnSuccessListener { result ->
                Picasso.get()
                    .load(result)
                    .fit()
                    .centerCrop()
                    .into(holder.profile_imageView)
            }
            .addOnFailureListener {
                //Log.w(TAG, it) // exception is already printed in StorageException class
                Log.d(TAG, "This user does not have a profile picture!")
            }
    }


}