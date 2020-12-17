package com.example.virtualbreak.controller.adapters.singlegroup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.virtualbreak.R
import java.util.*


class SingleGroupRoomsAdapter(context: Context, resource: Int, objects: List<SingleGroupRoom>) :
    ArrayAdapter<SingleGroupRoom?>(context, resource, objects) {

    var items_list: List<SingleGroupRoom> = ArrayList<SingleGroupRoom>()
    var custom_layout_id: Int
    val TAG: String = "SingleGroupRoomsAdapter"

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
        if(v != null){
            val imageView = v.findViewById<ImageView>(R.id.room_imageview)
            val textView = v.findViewById<TextView>(R.id.room_text)

            // get the item using the  position param
            val item: SingleGroupRoom = items_list[position]
            imageView.setImageResource(item.image_id)
            textView.setText(item.text)
            return v
        }
        else{
            throw RuntimeException(TAG+ "getView view is NULL")
        }

    }

    init {
        items_list = objects
        custom_layout_id = resource
    }
}

