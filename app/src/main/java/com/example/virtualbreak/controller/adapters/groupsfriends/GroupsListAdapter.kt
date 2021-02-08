package com.example.virtualbreak.controller.adapters.groupsfriends

import android.util.Log
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.model.Group
import com.example.virtualbreak.view.view_fragments.groupsfriends.GroupsFriendsFragmentDirections


/**
 * This Adapter manages the Receyler View of the Groups in the groups_grouplist_fragment
 */
class GroupsListAdapter(groups: ArrayList<Group>, private val context: Context?) : RecyclerView.Adapter<GroupsListAdapter.ViewHolder>() {

    lateinit var view: View
    val TAG = "GroupsListAdapter"
    var groups: ArrayList<Group>

    init{
        this.groups = groups
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val  textView: TextView
        val groupImg: ImageView
        val cardView: CardView
        private val TAG: String = "GroupsListAdapter_ViewHolder"

        init{
            textView = itemView.findViewById(R.id.group_list_name)
            groupImg = itemView.findViewById(R.id.group_list_img)
            cardView = itemView.findViewById(R.id.group_list_item_card)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(viewGroup.context).inflate(R.layout.group_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get pulled groups, transform HashMap in ArrayList, get group description
        holder.textView.text = groups[position].description

        groups[position].rooms?.let{
            if(it.size > 0){
                //there are open rooms in this group
                context?.let{
                    holder.groupImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cup_black))
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(
                        context,
                        R.color.tea_green2
                    ))
                }
            }
        }

        view.setOnClickListener{
            val group = groups[position]
            val groupId = group.uid
            //SharedPrefManager.instance.saveGroupId(groupId)
            Log.d(TAG, "clicked on group "+groupId)
            val action = GroupsFriendsFragmentDirections.actionNavHomeToSingleGroupFragment(groupId)
            Navigation.findNavController(view).navigate(action)
        }

    }

    override fun getItemCount(): Int {
        if (groups == null)
            return 0
        else
            return groups.size
    }

    fun updateData(groups: ArrayList<Group>){
        this.groups = groups
        notifyDataSetChanged()
    }















}