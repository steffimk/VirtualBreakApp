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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.SingleGroupRoomsAdapter
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.Roomtype
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.nambimobile.widgets.efab.FabOption

class SingleGroupFragment : Fragment() {

    private val TAG: String = "SingleGroupFragment"

    //Navigation argument to pass selected group id from GroupsFriendsFragment (GroupsListAdapter) to SingleGroupFragment
    val args: SingleGroupFragmentArgs by navArgs()
    private lateinit var groupId: String

    private val singleGroupViewModel: SingleGroupViewModel by viewModels { SingleGroupViewModelFactory(args.groupId) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_singlegroup, container, false)

        val textView: TextView = root.findViewById(R.id.text_singlegroup)
        singleGroupViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        groupId = args.groupId
        val gridView: GridView = root.findViewById(R.id.grid_view)

        // Observe whether rooms changed
        singleGroupViewModel.getRooms().observe(viewLifecycleOwner,
            Observer<HashMap<String, Room>>{ roomsMap ->
                Log.d(TAG, "Observed rooms: $roomsMap")
                val customAdapter =
                    context?.let {
                        SingleGroupRoomsAdapter(it, R.layout.singlegroup_room_list_item, ArrayList(roomsMap.values))
                    }
                gridView.adapter = customAdapter
        })


        val fab: FloatingActionButton = root.findViewById(R.id.fab_singlegroup)

        val fabOptionNormal: FabOption = root.findViewById(R.id.fab_singlegroup_option1)

        fabOptionNormal.setOnClickListener {
            //TODO Send notificaitons, go to created Breakroom

            //Save the Breakroom with intent coffee

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
           if (groupId != null && groupId != "") {
                PushData.saveRoom(groupId, Roomtype.GAME, "Games")
                Snackbar.make(root, "Öffne neuen Pausenraum", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

            //TODO Make another Button for Sport?
        }

        return root
    }
}