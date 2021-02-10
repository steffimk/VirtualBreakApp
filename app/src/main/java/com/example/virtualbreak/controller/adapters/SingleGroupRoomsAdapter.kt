package com.example.virtualbreak.controller.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.Roomtype
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.view.view_activitys.breakroom.BreakRoomActivity
import com.example.virtualbreak.view.view_activitys.breakroom.BreakroomWidgetService

/**
 * This adapter manages the grid view of open Breakrooms in a group
 */
class SingleGroupRoomsAdapter(context: Context, rooms: ArrayList<Room>, userName:String?) : RecyclerView.Adapter<SingleGroupRoomsAdapter.ViewHolderRooms>() {

    lateinit var view: View
    var username: String?
    var rooms_list: ArrayList<Room> = ArrayList<Room>()
    var context: Context

    val TAG: String = "SingleGroupRoomsAdapter"

    init {
        this.rooms_list = rooms
        this.username = userName
        this.context = context
    }

    class ViewHolderRooms(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.room_imageview)
        val roomName: TextView = itemView.findViewById(R.id.room_text)
        val participantCount: TextView = itemView.findViewById(R.id.room_participants_count)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderRooms {
        view = LayoutInflater.from(viewGroup.context).inflate(R.layout.singlegroup_room_list_item, viewGroup, false)

        return ViewHolderRooms(view)
    }

    override fun getItemCount(): Int {
        return rooms_list.size
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolderRooms, position: Int) {
        // get the item using the  position param
        val item: Room = rooms_list[position]

        //If room has no members: delete and don't show
        if (item.users.size == 0) {
            PushData.deleteRoom(item)
        }
        holder.imageView.setImageResource(item.type.symbol)
        holder.roomName.text = item.description
        holder.participantCount.text = item.users.size.toString()

        //check if user in still in Room, if yes deactivate Click on rooms except for current

        if (SharedPrefManager.instance.getRoomId() == null) {
            view.setOnClickListener {
                prepareAndInitBreakStatus() //before saving roomid in SharedPrefs
                PushData.joinRoom(context, item.uid, username)
                SharedPrefManager.instance.saveRoomId(item.uid)

                val intent = Intent(context, BreakRoomActivity::class.java)
                intent.putExtra(Constants.USER_NAME, username)
                intent.putExtra(Constants.ROOM_TYPE, item.type.dbStr)
                if (item.type == Roomtype.GAME) {
                    intent.putExtra(Constants.GAME_ID, item.gameId)
                }
                context.startActivity(intent)
            }
        } else {
            if (SharedPrefManager.instance.getRoomId() == item.uid) {
                view.setOnClickListener {
                    context.stopService(Intent(context, BreakroomWidgetService::class.java))

                    val intent = Intent(context, BreakRoomActivity::class.java)
                    intent.putExtra(Constants.USER_NAME, username)
                    intent.putExtra(Constants.ROOM_TYPE, item.type.dbStr)
                    if (item.type == Roomtype.GAME) {
                        intent.putExtra(Constants.GAME_ID, item.gameId)
                    }
                    context.startActivity(intent)
                }
            } else {
                val card = view as CardView
                card.setCardBackgroundColor(Color.parseColor("#bdbdbd"))
                view.setOnClickListener {
                    //Do Nothing and override existing onCLickListener
                }
            }
        }


    }

    private fun prepareAndInitBreakStatus() {
        //save current status (before break) in SharedPrefs
        PullData.currentStatus?.let { it ->
            if(SharedPrefManager.instance.getRoomId() == null || "".equals(SharedPrefManager.instance.getRoomId())){ //only save status before going in breakroom if about to enter new room (not reenter)
                Log.d(TAG, "saveCurrentStatus "+it.dbStr)
                SharedPrefManager.instance.saveCurrentStatus(
                    it
                )
            }
        }
        //automatically set status to INBREAK
        PushData.setStatus(Status.INBREAK)
    }

    fun updateData(rooms: ArrayList<Room>){
        rooms_list = rooms
        notifyDataSetChanged()
    }

}

