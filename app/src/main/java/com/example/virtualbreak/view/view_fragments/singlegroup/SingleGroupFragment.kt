package com.example.virtualbreak.view.view_fragments.singlegroup

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.adapters.SingleGroupRoomsAdapter
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.Roomtype
import com.example.virtualbreak.view.view_activitys.breakroom.BreakRoomActivity
import com.example.virtualbreak.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.nambimobile.widgets.efab.FabOption
import kotlinx.android.synthetic.main.fragment_singlegroup.*
import kotlinx.android.synthetic.main.fragment_myprofile.*

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

        var userName: String? = null

        singleGroupViewModel.getUser().observe(viewLifecycleOwner, Observer<User> { observedUser ->
            if (observedUser != null) {
                userName = observedUser.username
            }
        })

        // Observe whether rooms changed
        singleGroupViewModel.getRooms().observe(viewLifecycleOwner,
            Observer<HashMap<String, Room>> { roomsMap ->
                Log.d(TAG, "Observed rooms: $roomsMap")
                val customAdapter =
                    context?.let {
                        SingleGroupRoomsAdapter(it, R.layout.singlegroup_room_list_item, ArrayList(roomsMap.values),userName)
                    }
                gridView.adapter = customAdapter
            })


        val fab: FloatingActionButton = root.findViewById(R.id.fab_singlegroup)

        val fabOptionCoffee: FabOption = root.findViewById(R.id.fab_singlegroup_option1)
        fabOptionCoffee.setOnClickListener {
            openBreakroom(Roomtype.COFFEE, context)
        }

        val fabOptionQuestion: FabOption = root.findViewById(R.id.fab_singlegroup_option2)
        fabOptionQuestion.setOnClickListener {
            openBreakroom(Roomtype.QUESTION, context)

        }

        val fabOptionGame: FabOption = root.findViewById(R.id.fab_singlegroup_option3)
        fabOptionGame.setOnClickListener {
            openBreakroom(Roomtype.GAME, context)
        }

        val fabOptionSport: FabOption = root.findViewById(R.id.fab_singlegroup_option4)
        fabOptionSport.setOnClickListener {
            openBreakroom(Roomtype.SPORT, context)
        }

        return root
    }


    private fun openBreakroom(roomtype: Roomtype, thisContext: Context?) {

        Log.d(TAG, "create $roomtype Breakroom")

        var roomId: String? = null
        if (groupId != "") {
            roomId = PushData.saveRoom(groupId, roomtype, roomtype.dbStr)
            SharedPrefManager.instance.saveRoomId(roomId!!)
        }
        val intent = Intent(activity, BreakRoomActivity::class.java)
        activity?.startActivity(intent)
        //TODO send notification to friends

    }
}