package com.example.virtualbreak.controller.adapters.groupsfriends

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Friend
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.model.User
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.friend_list_item.view.*

class SearchFriendListAdapter: RecyclerView.Adapter<SearchFriendListAdapter.ViewHolderFriends>() {

    private var testNames: HashMap<String,User> = HashMap()

    var allFriends = arrayListOf<Friend>()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener?){
        this.onClick = mOnItemClickListener
    }


    inner class ViewHolderFriends(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.friend_list_name)
        val profilPicture = itemView.findViewById<RoundedImageView>(R.id.friend_list_image)
        val selectBox = itemView.findViewById<CheckBox>(R.id.friends_select_box)
        val status = itemView.findViewById<TextView>(R.id.friend_list_status)


    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderFriends {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.friend_list_item, viewGroup, false)

        return ViewHolderFriends(view)
    }

    override fun getItemCount(): Int {
        return allFriends.size
    }


    override fun onBindViewHolder(holder: ViewHolderFriends, position: Int) {
       // set all the contents in a view
        //holder.textView.text = testNames[position]
        //Convert friends Hashmap to Friendlist

        val viewHolder: ViewHolderFriends = holder
        val friend = allFriends[position]

        viewHolder.selectBox.visibility = View.VISIBLE
        viewHolder.username.text = friend.username
        //ToDo set the profilpicture
        //Todo set status?
        //viewHolder.status.visibility = View.GONE

        if (onClick != null) viewHolder.itemView.setOnClickListener{
            Log.d("Groups","onClick")
            friend.isSelectet = !friend.isSelectet
            viewHolder.selectBox.isChecked = friend.isSelectet

            //notifyDataSetChanged()
            notifyItemChanged(position);
            Log.d("Groups","onClick $position ${friend.isSelectet}  ${viewHolder.username.text} ${viewHolder.selectBox.isChecked}")
            //notifyDataSetChanged()
            onClick!!.onItemClick(friend)

        }

    }

    interface OnItemClickListener {
        fun onItemClick(friend: Friend)
    }

    fun getFriends(){
        Log.d("Groups","friends" + PullData.friends)
        //PullData.friends.forEach {
        testNames.forEach{
                (key, user) -> allFriends.add(Friend(user.uid, user.username, user.profilePicture, user.status, false))
        }
        Log.d("Groups","allFriends" + allFriends)

    }

    fun testFriends(){

        //TODO lösche methode und ersetze dummy daten durch echte

        testNames.put("Hannes", User("12356","Hannes", "test@mails.de",Status.BUSY))
        testNames.put("Gerda", User("12456","Gerda", "test@mails.de",Status.BUSY))
        testNames.put("Sui", User("14566","Susi", "test@mails.de",Status.BUSY))
        testNames.put("Manfred", User("167856","Manfred", "test@mails.de",Status.BUSY))
        testNames.put("Ulli", User("12356","Ulli", "test@mails.de",Status.BUSY))
        testNames.put("Bernd", User("12asd6","Bernd", "test@mails.de",Status.BUSY))
        testNames.put("Henning", User("1w453456","Hennig", "test@mails.de",Status.BUSY))
        testNames.put("Klara", User("1erted6","Klara", "test@mails.de",Status.BUSY))

        Log.d("Groups", "testFriends "+testNames)
    }


}
