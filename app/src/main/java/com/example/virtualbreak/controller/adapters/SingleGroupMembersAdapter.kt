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
import com.example.virtualbreak.model.User
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.view.view_fragments.singlegroup.SingleGroupFragment
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


/**
 * This Adapter manages the content of the Friendlist in the SingleGroupFragment to show the members of a group
 */

class SingleGroupMembersAdapter(
    friends: ArrayList<User>,
    private val context: Context?,
    private val singleGroupFragment: SingleGroupFragment
) : RecyclerView.Adapter<SingleGroupMembersAdapter.ViewHolderFriends>() {

    lateinit var view: View
    var friends: ArrayList<User>
    val TAG = "FriendListAdapter"

    init{
        this.friends = friends
    }

    class ViewHolderFriends(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textView: TextView = itemView.findViewById(R.id.singlegroup_member_name)
        val statusCircleImg: ImageView = itemView.findViewById(R.id.singlegroup_member_status)
        val profilePic: ImageView = itemView.findViewById(R.id.singlegroup_member_img)

        private val TAG: String = "FriendListAdapter_ViewHolder"

        init{

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderFriends {
        view = LayoutInflater.from(viewGroup.context).inflate(R.layout.singlegroup_member_item, viewGroup, false)

        return ViewHolderFriends(view)
    }

    override fun getItemCount(): Int {
        return friends.size
    }


    override fun onBindViewHolder(holder: ViewHolderFriends, position: Int) {
        holder.textView.text = friends[position].username

        loadProfilePicture(holder, friends[position].uid)

        context?.let{
            when(friends[position].status){
                Status.AVAILABLE -> holder.statusCircleImg.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_available))
                Status.BUSY -> holder.statusCircleImg.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_busy))
                Status.STUDYING -> holder.statusCircleImg.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_studying))
                Status.INBREAK -> holder.statusCircleImg.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_cup_black))
                Status.ABSENT -> holder.statusCircleImg.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_unknown))
                else -> holder.statusCircleImg.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_unknown))
            }
        }

        view.setOnClickListener {
            val friendId = friends[position].uid
            //evtl go to selected friend (evtl no need, if status shown in list) or show popup
            Log.d(TAG, "FriendId $friendId was clicked on")
            singleGroupFragment.expandFriendPopUp(holder.textView, friends[position])

        }

    }

    private fun loadProfilePicture(holder: ViewHolderFriends, userId: String) {

        val mStorageRef = FirebaseStorage.getInstance().getReference()
        mStorageRef.child("img/profilePics/$userId").downloadUrl
            .addOnSuccessListener { result ->
                Picasso.get()
                    .load(result)
                    .fit()
                    .centerCrop()
                    .into(holder.profilePic)
            }
            .addOnFailureListener {
                //Log.w(TAG, it) // exception is already printed in StorageException class
                Log.d(TAG, "This user does not have a profile picture!")
            }
    }

    fun updateData(newFriends: ArrayList<User>){
        this.friends = newFriends
        notifyDataSetChanged()
    }


}