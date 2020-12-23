package com.example.virtualbreak.view.view_fragments.singlegroup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.singlegroup.SingleGroupRoom
import com.example.virtualbreak.controller.adapters.singlegroup.SingleGroupRoomsAdapter
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Roomtype
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class SingleGroupFragment : Fragment() {

    private lateinit var singleGroupViewModel: SingleGroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        singleGroupViewModel =
            ViewModelProvider(this).get(SingleGroupViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_singlegroup, container, false)

        val textView: TextView = root.findViewById(R.id.text_singlegroup)
        singleGroupViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val prefs = this.context?.getSharedPreferences("com.example.virtualbreak", Context.MODE_PRIVATE)
        val groupId = prefs?.getString("com.example.virtualbreak.groupId", "")
        println("Got groupId from shared preferences: $groupId")
        val rooms = groupId?.let { PullData.getRoomsOfGroup(it) }

        val itemsList: MutableList<SingleGroupRoom> = ArrayList()
        rooms?.forEach{ room ->
            itemsList.add(SingleGroupRoom(room.type.symbol, room.type.dbStr))
        }

        val gridView: GridView = root.findViewById(R.id.grid_view)
        val customAdapter =
            context?.let { SingleGroupRoomsAdapter(it, R.layout.singlegroup_room_list_item, itemsList) }
        gridView.adapter = customAdapter


        val fab: FloatingActionButton = root.findViewById(R.id.fab_singlegroup)
        fab.setOnClickListener { view ->
            val groupId = this.context?.getSharedPreferences("com.example.virtualbreak", Context.MODE_PRIVATE)?.getString("com.example.virtualbreak.groupId", "")
            if (groupId != null && groupId != "") {
                PushData.saveRoom(groupId, Roomtype.COFFEE) // TODO: Let user decide on RoomType
                Snackbar.make(view, "Öffne neuen Pausenraum", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }

        return root
    }
}