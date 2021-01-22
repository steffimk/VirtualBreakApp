package com.example.virtualbreak.controller.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.view.view_activitys.breakroom.BreakRoomActivity


class SingleGroupRoomsAdapter(context: Context, resource: Int, objects: ArrayList<Room>, userName:String?) :
    ArrayAdapter<Room>(context, resource, objects) {

    var items_list: ArrayList<Room> = ArrayList<Room>()
    var custom_layout_id: Int
    val TAG: String = "SingleGroupRoomsAdapter"
    val userName = userName

    override fun getCount(): Int {
        return items_list.size
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            // getting reference to the main layout and
            // initializing
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(custom_layout_id, null)
        }

        // initializing the imageview and textview and
        // setting data
        if (v != null) {
            val imageView = v.findViewById<ImageView>(R.id.room_imageview)
            val roomName = v.findViewById<TextView>(R.id.room_text)
            val participantCount = v.findViewById<TextView>(R.id.room_participants_count)

            // get the item using the  position param
            val item: Room = items_list[position]
            imageView.setImageResource(item.type.symbol)
            roomName.text = item.description
            participantCount.text = item.users.size.toString()

            v.setOnClickListener {
                var context = imageView.context

                SharedPrefManager.instance.saveRoomId(item.uid)

                val intent = Intent(context, BreakRoomActivity::class.java)
                /*
                val roomId = "abc"
                intent.putExtra("room_id", roomId)
                */
                intent.putExtra(Constants.USER_NAME, userName)
                // TODO put roomType
                context.startActivity(intent)
            }


            return v
        } else {
            throw RuntimeException(TAG + "getView view is NULL")
        }

    }

    init {
        items_list = objects
        custom_layout_id = resource
    }
}

