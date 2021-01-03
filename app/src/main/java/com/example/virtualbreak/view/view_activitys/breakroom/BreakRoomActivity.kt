package com.example.virtualbreak.view.view_activitys.breakroom

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.ChatAdapter
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Message
import com.example.virtualbreak.model.Room
import kotlinx.android.synthetic.main.activity_break_room.*


class BreakRoomActivity : AppCompatActivity() {

    private val TAG = "BreakRoomActivity"

    private val viewModel: BreakRoomViewModel by viewModels { BreakRoomViewModelFactory(SharedPrefManager.instance.getRoomId()?:"") }
    private var room: Room? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break_room)

        val roomId = SharedPrefManager.instance.getRoomId()

        /*
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val roomId = bundle.getString("room_id")

            Toast.makeText(this, roomId, Toast.LENGTH_LONG).show()
        }
        */

        if (roomId != null) {
            PushData.joinRoom(roomId)
//            Toast.makeText(this, "not Null", Toast.LENGTH_LONG).show()

            var defaultMessages : MutableList<Message> = ArrayList()
            var defaultM = Message("default", "Keine Nachricht")
            defaultMessages.add(defaultM)

            chat_messages_recycler_view.layoutManager = LinearLayoutManager(this)
            chat_messages_recycler_view.adapter = ChatAdapter(this, defaultMessages)

            viewModel.getRoom().observe(this, Observer<Room>{ observedRoom ->

                room = observedRoom
                viewModel.loadUsersOfRoom(this)
                /*if (room != null && (room!!.users != observedRoom.users)) {
                    viewModel.loadUsersOfRoom(this)
                }*/
                Log.d(TAG, "Observed room: $observedRoom")
                if(observedRoom != null && observedRoom.messages != null && observedRoom.messages.isNotEmpty()){
                    val messages = observedRoom.messages
                    Log.i(TAG, "messagesList: $messages")
                    chat_messages_recycler_view.adapter = ChatAdapter(this, ArrayList(messages.values))
                }
            })
        } else {
            Toast.makeText(this, R.string.something_wrong, Toast.LENGTH_LONG).show()
            // ends activity and return to previous
            finish()
        }

        // makes textview scrollable
        //chat_messages_view.setMovementMethod(ScrollingMovementMethod())

        send_message_button.setOnClickListener {
            val input = chat_message_input.text
            val message = input.toString()
            if(!input.equals("")){
                if (roomId != null) {
                    PushData.sendMessage(roomId, message)
                }
            }
            input.clear()
        }


        // when leaving a room remove the roomId from preferences because it's not needed anymore and ends this activity
        leave_room_button.setOnClickListener {
            viewModel.getRoom().removeObservers(this)
            PushData.leaveRoom(room)
            SharedPrefManager.instance.removeRoomId()
            Log.d(TAG, "Left room $roomId")
            finish()
        }
    }
}