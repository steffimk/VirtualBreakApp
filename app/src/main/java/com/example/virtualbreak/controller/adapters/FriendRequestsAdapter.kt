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
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.FCMService
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.NotificationBody
import com.example.virtualbreak.model.NotificationData
import com.example.virtualbreak.model.PushNotification
import com.example.virtualbreak.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.squareup.picasso.Picasso
import java.io.IOException

class FriendRequestsAdapter(private val friendRequests: ArrayList<User>) : RecyclerView.Adapter<FriendRequestsAdapter.ViewHolderFriendRequests>() {

    lateinit var view: View
    val TAG = "FriendRequestsAdapter"

    class ViewHolderFriendRequests(itemView: View) : RecyclerView.ViewHolder(itemView){
        val acceptRequestBtn: Button
        val deleteRequestBtn: Button
        val username_textView: TextView
        val email_textView: TextView
        val profile_imageView: ImageView
        private val TAG: String = "FriendRequestsAdapter_ViewHolder"

        init{
            acceptRequestBtn = itemView.findViewById(R.id.friendrequest_accept)
            deleteRequestBtn = itemView.findViewById(R.id.friendrequest_delete)
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

        loadProfilePicture(holder, friendRequests[position].uid)

        //define click listener for viewholders view
        holder.acceptRequestBtn.setOnClickListener{

            val friendToBeAdded = friendRequests[position]
            val ownUserName = SharedPrefManager.instance.getUserName()
            PushData.confirmFriendRequest(friendToBeAdded.uid)
            // Send notification to user
            val title = "Freundschaft geschlossen"
            val message = "${ownUserName} hat deine Freundschaftsanfrage angenommen."
            PushNotification(
                NotificationData(title, message),
                NotificationBody(title, message),
                friendToBeAdded.fcmToken
            ).also {
                Log.d(TAG, "Sending notification: $it")
                FCMService.sendNotification(it)
            }
            Snackbar.make(view, ""+friendRequests[position].username+" wurde zu deiner Freundeliste hinzugefügt!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        holder.deleteRequestBtn.setOnClickListener {
            PushData.removeFriendRequest(friendRequests[position].uid)
            Snackbar.make(view, "Du hast die Freundschaftsanfrage von ${friendRequests[position].username} gelöscht", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun loadProfilePicture(holder: FriendRequestsAdapter.ViewHolderFriendRequests, userId: String) {

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