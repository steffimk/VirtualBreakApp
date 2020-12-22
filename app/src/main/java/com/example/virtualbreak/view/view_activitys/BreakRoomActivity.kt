package com.example.virtualbreak.view.view_activitys

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualbreak.R
import kotlinx.android.synthetic.main.activity_break_room.*


class BreakRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break_room)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val roomId = bundle.getString("room_id")

            Toast.makeText(this, roomId, Toast.LENGTH_LONG).show()
            // TODO get content from room with id roomId
        }

        // makes textview scrollable
        chat_messages_view.setMovementMethod(ScrollingMovementMethod())

        send_message_button.setOnClickListener {
            // TODO: save entered message

            val input = chat_message_input.text
            input.clear()
        }
    }
}