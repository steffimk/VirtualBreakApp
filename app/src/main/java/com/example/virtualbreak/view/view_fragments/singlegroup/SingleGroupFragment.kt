package com.example.virtualbreak.view.view_fragments.singlegroup

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.adapters.SingleGroupRoomsAdapter
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Roomtype
import com.example.virtualbreak.view.view_activitys.BreakRoomActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.nambimobile.widgets.efab.FabOption

class SingleGroupFragment : Fragment() {

    private lateinit var singleGroupViewModel: SingleGroupViewModel
    private val TAG: String = "SingleGroupFragment"

    //Navigation argument to pass selected group id from GroupsFriendsFragment (GroupsListAdapter) to SingleGroupFragment
    val args: SingleGroupFragmentArgs by navArgs()
    private lateinit var groupId: String

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

        groupId = args.groupId

        var rooms = PullData.getRoomsOfGroup(groupId)

        val gridView: GridView = root.findViewById(R.id.grid_view)
        if (!rooms.isEmpty()) {
            val customAdapter =
                context?.let {
                    SingleGroupRoomsAdapter(it, R.layout.singlegroup_room_list_item, rooms)
                }
            gridView.adapter = customAdapter
        }


        // Observe whether rooms changed
        PullData.rooms.observe(viewLifecycleOwner, {
            val newRooms = PullData.getRoomsOfGroup(groupId)
            Log.d(TAG, "Former rooms: $rooms")
            Log.d(TAG, "New rooms: $newRooms")
            if (newRooms.equals(rooms) == false) {
                Log.d(TAG, "Observed change in rooms of group")
                rooms = newRooms
                gridView.adapter = context?.let { SingleGroupRoomsAdapter(it, R.layout.singlegroup_room_list_item, newRooms) } // TODO: Change adapter instead of creating new one?
            } else {
                Log.d(TAG, "Observed no new rooms")
            }
        })


        val fab: FloatingActionButton = root.findViewById(R.id.fab_singlegroup)

        val fabOptionNormal: FabOption = root.findViewById(R.id.fab_singlegroup_option1)

        fabOptionNormal.setOnClickListener {
            //TODO Send notificaitons, go to created Breakroom

            //Save the Breakroom with intent coffee
            val groupId = this.context?.getSharedPreferences("com.example.virtualbreak", Context.MODE_PRIVATE)?.getString("com.example.virtualbreak.groupId", "")
            if (groupId != null && groupId != "") {
                PushData.saveRoom(groupId, Roomtype.COFFEE, "Kaffe trinken")
                Snackbar.make(root, "Öffne neuen Pausenraum", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }


        }
        val fabOptionQuestion: FabOption = root.findViewById(R.id.fab_singlegroup_option2)
        fabOptionQuestion.setOnClickListener {
            //TODO Send notificaitons, go to created Breakroom

            //Save the BreakRoom with Intent Sport
            val groupId = this.context?.getSharedPreferences("com.example.virtualbreak", Context.MODE_PRIVATE)?.getString("com.example.virtualbreak.groupId", "")
            if (groupId != null && groupId != "") {
                PushData.saveRoom(groupId, Roomtype.QUESTION, "Kaffee trinken")
                Snackbar.make(root, "Öffne neuen Pausenraum", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
        val fabOptionGame: FabOption = root.findViewById(R.id.fab_singlegroup_option3)
        fabOptionGame.setOnClickListener {
            //TODO Send notificaitons, go to created Breakroom

            // save the Breakroom with intent Game
            val groupId = this.context?.getSharedPreferences("com.example.virtualbreak", Context.MODE_PRIVATE)?.getString("com.example.virtualbreak.groupId", "")
            if (groupId != null && groupId != "") {
                PushData.saveRoom(groupId, Roomtype.GAME, "Games")
                Snackbar.make(root, "Öffne neuen Pausenraum", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

            //TODO Make another Button for Sport?
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        PullData.rooms.value = PullData.rooms.value // Notify observers
    }
}