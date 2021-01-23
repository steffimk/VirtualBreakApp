package com.example.virtualbreak.view.view_fragments.singlegroup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.adapters.SingleGroupRoomsAdapter
import com.example.virtualbreak.controller.communication.FCMService
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.*
import com.example.virtualbreak.view.view_activitys.breakroom.BreakRoomActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nambimobile.widgets.efab.FabOption



private const val GROUP_ID = "groupId"

class SingleGroupRoomsFragment : Fragment() {

    private val TAG: String = "SingleGroupFragment"

    private var groupId: String? = null
    private lateinit var groupUsers: HashMap<String, User>

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
        val root = inflater.inflate(R.layout.fragment_singlegroup_rooms, container, false)

        groupId?.let{

            val singleGroupViewModel: SingleGroupViewModel by viewModels { SingleGroupViewModelFactory(it) }

            singleGroupViewModel.pullGroupWithId(it)
            singleGroupViewModel.getGroupUsers() // to trigger start init by lazy

            val gridView: GridView = root.findViewById(R.id.grid_view)

            var userName: String? = SharedPrefManager.instance.getUserName()

            // Observe whether rooms changed
            singleGroupViewModel.getRooms().observe(viewLifecycleOwner,
                { roomsMap ->
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
                    gridView.adapter = customAdapter //TODO reuse old adapter
                })

            singleGroupViewModel.getGroupUsers().observe(viewLifecycleOwner, {
                groupUsers = it
            })


            val fab: FloatingActionButton = root.findViewById(R.id.fab_singlegroup)

            val fabOptionCoffee: FabOption = root.findViewById(R.id.fab_singlegroup_option1)
            fabOptionCoffee.setOnClickListener {
                openBreakroom(Roomtype.COFFEE, singleGroupViewModel, userName)
            }

            val fabOptionQuestion: FabOption = root.findViewById(R.id.fab_singlegroup_option2)
            fabOptionQuestion.setOnClickListener {
                openBreakroom(Roomtype.QUESTION, singleGroupViewModel, userName)

            }

            val fabOptionGame: FabOption = root.findViewById(R.id.fab_singlegroup_option3)
            fabOptionGame.setOnClickListener {
                openBreakroom(Roomtype.GAME, singleGroupViewModel, userName)
            }

            val fabOptionSport: FabOption = root.findViewById(R.id.fab_singlegroup_option4)
            fabOptionSport.setOnClickListener {
                openBreakroom(Roomtype.SPORT, singleGroupViewModel, userName)
            }
        }





        return root
    }


    private fun openBreakroom(roomtype: Roomtype, singleGroupViewModel:SingleGroupViewModel ,userName:String?) {

        Log.d(TAG, "create $roomtype Breakroom")

        groupId?.let{
            if (groupId != "") {
                val roomId = PushData.saveRoom(it, roomtype, roomtype.dbStr)
                //sendNotifications(it, singleGroupViewModel.currentGroup?.description, roomtype.dbStr)
                sendNotifications(it, singleGroupViewModel, roomtype.dbStr)

                if(roomId != null){
                    context?.let{
                        PushData.joinRoom(it, roomId, userName)
                    }
                    SharedPrefManager.instance.saveRoomId(roomId)
                } else{
                    Log.w(TAG, "Room ID is null, can't open Breakroom")
                }
            }
        }

        val intent = Intent(activity, BreakRoomActivity::class.java)
        intent.putExtra(Constants.USER_NAME, userName)
        activity?.startActivity(intent)

    }

    private fun sendNotifications(groupId: String, singleGroupViewModel: SingleGroupViewModel, roomType: String) {
        val groupName = singleGroupViewModel.currentGroup?.description ?: ""
        val title = "Neuer Pausenraum in $groupName"
        val message = "${SharedPrefManager.instance.getUserName()} hat eine neue $roomType-Pause erstellt"
        Log.d(TAG, "Send notifications to group : $groupId")
        for( (userId, user) in this.groupUsers){
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