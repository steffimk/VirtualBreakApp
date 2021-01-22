package com.example.virtualbreak.view.view_fragments.singlegroup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.GridView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.adapters.SingleGroupRoomsAdapter
import com.example.virtualbreak.controller.communication.FCMService
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.*
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.Roomtype
import com.example.virtualbreak.model.User
import com.example.virtualbreak.view.view_activitys.NavigationDrawerActivity
import com.example.virtualbreak.view.view_activitys.VideoCallActivity
import com.example.virtualbreak.view.view_activitys.breakroom.BreakRoomActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nambimobile.widgets.efab.FabOption
import kotlinx.android.synthetic.main.fragment_groups_friendlist_fragment.*
import kotlinx.android.synthetic.main.fragment_singlegroup.*

class SingleGroupFragment : Fragment() {

    private val TAG: String = "SingleGroupFragment"

    //Navigation argument to pass selected group from GroupsFriendsFragment (GroupsListAdapter) to SingleGroupFragment
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

        setHasOptionsMenu(true);

        groupId = args.groupId
        singleGroupViewModel.pullGroupWithId(groupId)
        singleGroupViewModel.getGroupUsersWithFcmToken() // to trigger start init by lazy

        val gridView: GridView = root.findViewById(R.id.grid_view)

        var userName: String? = SharedPrefManager.instance.getUserName()

        // Observe whether rooms changed
        singleGroupViewModel.getRooms().observe(viewLifecycleOwner,
            Observer<HashMap<String, Room>> { roomsMap ->
                Log.d(TAG, "Observed rooms: $roomsMap")
                val customAdapter =
                    context?.let {
                        SingleGroupRoomsAdapter(
                            it,
                            R.layout.singlegroup_room_list_item,
                            ArrayList(roomsMap.values),
                            userName
                        )
                    }
                gridView.adapter = customAdapter
            })


        val fab: FloatingActionButton = root.findViewById(R.id.fab_singlegroup)

        val fabOptionCoffee: FabOption = root.findViewById(R.id.fab_singlegroup_option1)
        fabOptionCoffee.setOnClickListener {
            openBreakroom(Roomtype.COFFEE, context, userName)
        }

        val fabOptionQuestion: FabOption = root.findViewById(R.id.fab_singlegroup_option2)
        fabOptionQuestion.setOnClickListener {
            openBreakroom(Roomtype.QUESTION, context, userName)

        }

        val fabOptionGame: FabOption = root.findViewById(R.id.fab_singlegroup_option3)
        fabOptionGame.setOnClickListener {
            openBreakroom(Roomtype.GAME, context, userName)
        }

        val fabOptionSport: FabOption = root.findViewById(R.id.fab_singlegroup_option4)
        fabOptionSport.setOnClickListener {
            openBreakroom(Roomtype.SPORT, context, userName)
        }

        return root
    }


    private fun openBreakroom(roomtype: Roomtype, thisContext: Context?, userName:String?) {

        Log.d(TAG, "create $roomtype Breakroom")

        var roomId: String? = null
        if (groupId != "") {
            roomId = PushData.saveRoom(groupId, roomtype, roomtype.dbStr)
            sendNotifications(groupId, this.singleGroupViewModel.currentGroup?.description, roomtype.dbStr)
            SharedPrefManager.instance.saveRoomId(roomId!!)
        }
        val intent = Intent(activity, BreakRoomActivity::class.java)
        intent.putExtra(Constants.USER_NAME, userName)
        intent.putExtra(Constants.ROOM_TYPE, roomtype.dbStr)
        activity?.startActivity(intent)

    }

    private fun sendNotifications(groupId: String, groupDescription: String?, roomType: String) {
        val groupName = groupDescription ?: ""
        val title = "Neuer Pausenraum in $groupName"
        val message = "${SharedPrefManager.instance.getUserName()} hat eine neue $roomType -Pause erstellt"
        Log.d(TAG, "Send notifications to group : $groupId")
        for( (userId, pair) in this.singleGroupViewModel.getGroupUsersWithFcmToken()){
            val fcmToken = pair.first
            val userIsBusy = pair.second
            // Don't send notification to user with status "busy" and don't send to oneself
            if (!userIsBusy && userId != SharedPrefManager.instance.getUserId()) {
                Log.d(TAG, "Send notification to token: $fcmToken")
                PushNotification(
                    NotificationData(title, message),
                    NotificationBody(title, message),
                    fcmToken
                ).also {
                    Log.d(TAG, "Sending notification: $it")
                    FCMService.sendNotification(it)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.singlegroup_menu, menu)
        super.onCreateOptionsMenu(menu, inflater);
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {

        R.id.action_leave_group -> {
            singleGroupViewModel.currentGroup?.let { PushData.leaveGroup(it) }
            view?.findNavController()?.navigate(R.id.action_singleGroupFragment_to_home)
            true
        }
        //TODO edit group name? maybe in extra fragment
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}