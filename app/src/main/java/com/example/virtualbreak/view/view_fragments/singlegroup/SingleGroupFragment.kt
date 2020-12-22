package com.example.virtualbreak.view.view_fragments.singlegroup

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

        //TODO get room data from database + show users in room
        val itemsList: MutableList<SingleGroupRoom> = ArrayList()
        itemsList.add(SingleGroupRoom(R.drawable.home, "Mensa"))
        itemsList.add(SingleGroupRoom(R.drawable.addfriend, "Stucafe"))
        itemsList.add(SingleGroupRoom(R.drawable.exit, "Games"))

        val gridView: GridView = root.findViewById(R.id.grid_view)
        val customAdapter =
            context?.let { SingleGroupRoomsAdapter(it, R.layout.singlegroup_room_list_item, itemsList) }
        gridView.adapter = customAdapter


        val fab: FloatingActionButton = root.findViewById(R.id.fab_singlegroup)
        fab.setOnClickListener { view ->
             Snackbar.make(view, "Öffne neuen Pausenraum", Snackbar.LENGTH_LONG)
                 .setAction("Action", null).show()
        }

        return root
    }
}