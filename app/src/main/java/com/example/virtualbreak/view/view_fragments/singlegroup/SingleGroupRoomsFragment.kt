package com.example.virtualbreak.view.view_fragments.singlegroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.adapters.SingleGroupRoomsAdapter
import com.example.virtualbreak.controller.communication.FCMService
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.*
import com.example.virtualbreak.view.view_activitys.breakroom.BreakRoomActivity
import com.nambimobile.widgets.efab.ExpandableFab
import com.nambimobile.widgets.efab.FabOption
import kotlinx.android.synthetic.main.fragment_singlegroup_rooms.*


private const val GROUP_ID = "groupId"

/**
 * Fragment shows open rooms in this group as a grid view and a button to open a new break room.
 * When the user is already in a break, the rooms are greyed out and cannot be entered
 */
class SingleGroupRoomsFragment : Fragment() {

    private val TAG: String = "SingleGroupFragment"

    private var groupId: String? = null
    private lateinit var groupUsers: HashMap<String, User>
    var customAdapter: SingleGroupRoomsAdapter? = null
    private lateinit var root: View
    private lateinit var fabButton: ExpandableFab
    private lateinit var fabOptionCoffee: FabOption
    private lateinit var fabOptionQuestion: FabOption
    private lateinit var fabOptionGame: FabOption
    private lateinit var fabOptionSport: FabOption


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getString(GROUP_ID)
        }
    }

    //private val singleGroupViewModel: SingleGroupViewModel by viewModels { SingleGroupViewModelFactory(groupId) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_singlegroup_rooms, container, false)
        return root
    }

    /**
     * method called after viwes created, so put logic here to prevent nullpointerexcpetions
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupId?.let{
            val singleGroupViewModel: SingleGroupViewModel by viewModels { SingleGroupViewModelFactory(
                it
            ) }

            singleGroupViewModel.getCurrentGroup() // to trigger start init by lazy
            singleGroupViewModel.getGroupUsers() // to trigger start init by lazy

            grid_view.setHasFixedSize(true)

            val gridLayoutManager = GridLayoutManager(context, 2)
            grid_view.setLayoutManager(gridLayoutManager)
            grid_view.setItemAnimator(DefaultItemAnimator())

            var userName: String? = SharedPrefManager.instance.getUserName()

            // Observe whether rooms changed
            singleGroupViewModel.getRooms().observe(viewLifecycleOwner,
                { roomsMap ->
                    Log.d(TAG, "Observed rooms: $roomsMap")
                    customAdapter =
                        context?.let {
                            SingleGroupRoomsAdapter(
                                it,
                                ArrayList(roomsMap.values),
                                userName
                            )
                        }
                    grid_view.adapter = customAdapter

                })

            singleGroupViewModel.getGroupUsers().observe(viewLifecycleOwner, {
                groupUsers = it
            })
            fabButton = root.findViewById(R.id.fab_singlegroup)

            fabOptionCoffee = root.findViewById(R.id.fab_singlegroup_option1)
            fabOptionCoffee.setOnClickListener {
                checkIfInBreakOrOpenBreakRoom(Roomtype.COFFEE, singleGroupViewModel, userName)
            }

            fabOptionQuestion = root.findViewById(R.id.fab_singlegroup_option2)
            fabOptionQuestion.setOnClickListener {
                checkIfInBreakOrOpenBreakRoom(Roomtype.QUESTION, singleGroupViewModel, userName)

            }

            fabOptionGame = root.findViewById(R.id.fab_singlegroup_option3)
            fabOptionGame.setOnClickListener {
                checkIfInBreakOrOpenBreakRoom(Roomtype.GAME, singleGroupViewModel, userName)
            }

            fabOptionSport = root.findViewById(R.id.fab_singlegroup_option4)
            fabOptionSport.setOnClickListener {
                checkIfInBreakOrOpenBreakRoom(Roomtype.SPORT, singleGroupViewModel, userName)
            }
        }
    }


    fun checkIfInBreakOrOpenBreakRoom(
        roomType: Roomtype,
        singleGroupViewModel: SingleGroupViewModel,
        userName: String?,
    ) {
        if (SharedPrefManager.instance.getRoomId() != null) {
            Toast.makeText(
                context, R.string.toast_already_in_break,
                Toast.LENGTH_LONG
            ).show()
        } else {
            openBreakroom(roomType, singleGroupViewModel, userName)
        }
    }


    private fun openBreakroom(
        roomtype: Roomtype,
        singleGroupViewModel: SingleGroupViewModel,
        userName: String?
    ) {

        Log.d(TAG, "create $roomtype Breakroom")

        val intent = Intent(activity, BreakRoomActivity::class.java)

        groupId?.let{
            if (groupId != "") {
                val roomId = PushData.saveRoom(it, roomtype, roomtype.dbStr)
                //sendNotifications(it, singleGroupViewModel.currentGroup?.description, roomtype.dbStr)
                sendNotifications(it, singleGroupViewModel, roomtype.dbStr)

                if(roomId != null){
                    context?.let{
                        PushData.joinRoom(it, roomId, userName)
                    }

                    prepareAndInitBreakStatus() //before save roomId in SharedPrefs
                    SharedPrefManager.instance.saveRoomId(roomId)
                    if(roomtype == Roomtype.GAME){
                        val gameId = PushData.createGame(roomId)
                        intent.putExtra(Constants.GAME_ID, gameId)
                    }
                } else{
                    Log.w(TAG, "Room ID is null, can't open Breakroom")
                }
            }
        }

        intent.putExtra(Constants.USER_NAME, userName)
        intent.putExtra(Constants.ROOM_TYPE, roomtype.dbStr)
        activity?.startActivity(intent)

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

    private fun sendNotifications(
        groupId: String,
        singleGroupViewModel: SingleGroupViewModel,
        roomType: String
    ) {
        val groupName = singleGroupViewModel.getCurrentGroup().value?.description ?: ""
        val title = "Neuer Pausenraum in $groupName"
        val message = "${SharedPrefManager.instance.getUserName()} hat eine neue $roomType-Pause erstellt"
        Log.d(TAG, "Send notifications to group : $groupId")
        for((userId, user) in this.groupUsers){
            val fcmToken = user.fcmToken
            val userIsBusy = user.status == Status.BUSY
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param groupId Parameter 1. group ID
         * @return A new instance of fragment SingleGroupMembersFragment.
         */
        @JvmStatic
        fun newInstance(groupId: String) =
            SingleGroupRoomsFragment().apply {
                arguments = Bundle().apply {
                    putString(GROUP_ID, groupId)
                }
            }
    }


}