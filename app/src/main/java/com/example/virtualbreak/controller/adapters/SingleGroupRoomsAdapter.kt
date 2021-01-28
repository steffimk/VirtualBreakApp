package com.example.virtualbreak.controller.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

    class ViewHolderRooms(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView = itemView.findViewById<ImageView>(R.id.room_imageview)
        val roomName = itemView.findViewById<TextView>(R.id.room_text)
        val participantCount = itemView.findViewById<TextView>(R.id.room_participants_count)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderRooms {
        view = LayoutInflater.from(viewGroup.context).inflate(R.layout.singlegroup_room_list_item, viewGroup, false)

        return ViewHolderRooms(view)
    }

    override fun getItemCount(): Int {
        return rooms_list.size
    }


    override fun onBindViewHolder(holder: ViewHolderRooms, position: Int) {

        // get the item using the  position param
        val item: Room = rooms_list[position]
        holder.imageView.setImageResource(item.type.symbol)
        holder.roomName.text = item.description
        holder.participantCount.text = item.users.size.toString()

        view.setOnClickListener {

            PushData.joinRoom(context, item.uid, username)
            SharedPrefManager.instance.saveRoomId(item.uid)

            prepareAndInitBreakStatus()

            val intent = Intent(context, BreakRoomActivity::class.java)
            intent.putExtra(Constants.USER_NAME, username)
            intent.putExtra(Constants.ROOM_TYPE, item.type.dbStr)
            if (item.type.equals(Roomtype.GAME)){
                intent.putExtra(Constants.GAME_ID, item.gameId)
            }
            context.startActivity(intent)
        }

    }

    private fun prepareAndInitBreakStatus() {
        //save current status (before break) in SharedPrefs
        PullData.currentUser.value?.status?.let { it ->
            if(it != Status.INBREAK){ //only save status before going in breakroom if not status inbreak (otherwise might be have left and reentered breakroom)
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

