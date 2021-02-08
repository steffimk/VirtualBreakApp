package com.example.virtualbreak.controller.adapters.groupsfriends

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.model.User
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

/**
 * This adapter manages the friend list in AddGroupFragment, when choosing friends to make a group
 */
class SearchFriendListAdapter (friends: ArrayList<User>, private val context: Context?): RecyclerView.Adapter<SearchFriendListAdapter.ViewHolderFriends>() {

    private val TAG = "SearchFriendListAdapter"
    var friends: ArrayList<User>

    init{
        this.friends = friends
    }

    //private var testNames: HashMap<String, User> = HashMap()

    var onClick: OnItemClickListener? = null

    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener?){
        this.onClick = mOnItemClickListener
    }


    inner class ViewHolderFriends(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.friend_list_name)
        val profilePicture = itemView.findViewById<ImageView>(R.id.friend_list_image)
        val selectBox = itemView.findViewById<CheckBox>(R.id.friends_select_box)
        val statusCircleImg: ImageView = itemView.findViewById(R.id.status_circle_img)

    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderFriends {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.friend_list_item, viewGroup, false)

        return ViewHolderFriends(view)
    }

    override fun getItemCount(): Int {
        return friends.size
    }


    override fun onBindViewHolder(holder: ViewHolderFriends, position: Int) {
       // set all the contents in a view
        val viewHolder: ViewHolderFriends = holder
        val friend = friends[position]

        loadProfilePicture(holder, friend.uid)

        viewHolder.selectBox.visibility = View.VISIBLE
        viewHolder.selectBox.isChecked = friend.isSelected

        Log.d(TAG, "Friends ${friends}")

        viewHolder.username.text = friend.username

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
        //viewHolder.status.visibility = View.GONE

        if (onClick != null) viewHolder.itemView.setOnClickListener{
            friend.isSelected = !friend.isSelected
            viewHolder.selectBox.isChecked = friend.isSelected

            notifyItemChanged(position)
            Log.d(
                TAG,
                "onClick $position ${friend.isSelected}  ${viewHolder.username.text} ${viewHolder.selectBox.isChecked}"
            )
            //notifyDataSetChanged()
            onClick!!.onItemClick(friend)

        }

        viewHolder.selectBox.setOnCheckedChangeListener{
                buttonView, isChecked -> friend.isSelected = isChecked
                //notifyDataSetChanged()
            //Update corresponding object in array to, so whenever new view is shown, it reads the neweset statue of object
            Log.d("TAG", "ChangeListener ${friend.isSelected} $position")
        }


    }

    interface OnItemClickListener {
        fun onItemClick(friend: User)
    }

    private fun loadProfilePicture(holder: SearchFriendListAdapter.ViewHolderFriends, userId: String) {

        val mStorageRef = FirebaseStorage.getInstance().getReference()
        mStorageRef.child("img/profilePics/$userId").downloadUrl
            .addOnSuccessListener { result ->
                Picasso.get()
                    .load(result)
                    .fit()
                    .centerCrop()
                    .into(holder.profilePicture)
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
