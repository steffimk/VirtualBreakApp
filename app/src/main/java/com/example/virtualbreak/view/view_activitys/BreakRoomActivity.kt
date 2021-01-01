package com.example.virtualbreak.view.view_activitys

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_break_room.*


class BreakRoomActivity : AppCompatActivity() {

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
            room = PullData.rooms.value?.get(roomId)

            Toast.makeText(this, "not Null", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, R.string.something_wrong, Toast.LENGTH_LONG).show()
            // ends activity and return to previous
            finish()
        }

        // makes textview scrollable
        chat_messages_view.setMovementMethod(ScrollingMovementMethod())

        send_message_button.setOnClickListener {
            // TODO: save entered message

            val input = chat_message_input.text
            input.clear()
        }


        // when leaving a room remove the roomId from preferences because it's not needed anymore and ends this activity
        leave_room_button.setOnClickListener {
            PushData.leaveRoom(room)
            SharedPrefManager.instance.removeRoomId()
            finish()
        }
    }
}