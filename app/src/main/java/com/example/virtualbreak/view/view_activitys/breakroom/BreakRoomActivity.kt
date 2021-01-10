package com.example.virtualbreak.view.view_activitys.breakroom

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.adapters.ChatAdapter
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Message
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.User
import com.example.virtualbreak.view.view_activitys.VideoCallActivity
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_break_room.*
import kotlinx.android.synthetic.main.app_bar_main.*


class BreakRoomActivity : AppCompatActivity() {

    private val TAG = "BreakRoomActivity"

    private val viewModel: BreakRoomViewModel by viewModels {
        BreakRoomViewModelFactory(
            SharedPrefManager.instance.getRoomId() ?: ""
        )
    }
    private var room: Room? = null

    lateinit var mtoolbar: Toolbar
    lateinit var editText: EditText
    private var userName: String? = null
    private val roomId: String? = SharedPrefManager.instance.getRoomId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break_room)

        //val roomId = SharedPrefManager.instance.getRoomId()
        //var userName : String? = null


        mtoolbar = findViewById(R.id.toolbar_breakroom)
        editText = findViewById(R.id.et_changeRoomName)

        setSupportActionBar(mtoolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_leave)


        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            userName = bundle.getString(Constants.USER_NAME)
        }


        if (roomId != null) {
            PushData.joinRoom(this, roomId, userName)

            var defaultMessages: MutableList<Message> = ArrayList()
            var defaultM = Message("default", "Keine Nachricht", Constants.DEFAULT_TIME)
            defaultMessages.add(defaultM)

            chat_messages_recycler_view.layoutManager = LinearLayoutManager(this)
            chat_messages_recycler_view.adapter = ChatAdapter(this, defaultMessages)

            viewModel.getUser().observe(this, Observer<User> { observedUser ->
                if (observedUser != null) {
                    userName = observedUser.username
                }
            })



            viewModel.getRoom().observe(this, Observer<Room> { observedRoom ->

                room = observedRoom
                viewModel.loadUsersOfRoom(this)

                if (observedRoom != null) {
                    supportActionBar?.title = observedRoom.description
                }
                /*if (room != null && (room!!.users != observedRoom.users)) {
                    viewModel.loadUsersOfRoom(this)
                }*/
                Log.d(TAG, "Observed room: $observedRoom")
                if (observedRoom != null && observedRoom.messages != null && observedRoom.messages.isNotEmpty()) {
                    val messages = observedRoom.messages
                    var messagesList = ArrayList(messages.values)
                    messagesList.sortBy { it.timestamp }
                    Log.i(TAG, "messagesList: $messages")
                    chat_messages_recycler_view.adapter = ChatAdapter(this, messagesList)
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
            if (!input.equals("")) {
                if (roomId != null) {
                    PushData.sendMessage(roomId, message)
                }
            }
            input.clear()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        android.R.id.home -> {
            // when leaving a room remove the roomId from preferences because it's not needed anymore and ends this activity
            viewModel.getRoom().removeObservers(this)
            PushData.leaveRoom(this, room, userName)
            SharedPrefManager.instance.removeRoomId()
            Log.d(TAG, "Left room $roomId")
            finish()
            true
        }

        R.id.action_edit -> {
            //User wants to edit the room name
            supportActionBar?.title = null
            editText.visibility = View.VISIBLE
            mtoolbar.menu.findItem(R.id.action_enter).isVisible = true
            mtoolbar.menu.findItem(R.id.action_edit).isVisible = false
            true
        }
        R.id.action_enter -> {
            val newTitle = editText.text.toString()
            mtoolbar.title = newTitle
            editText.visibility = View.GONE
            mtoolbar.menu.findItem(R.id.action_enter).isVisible = false
            mtoolbar.menu.findItem(R.id.action_edit).isVisible = true
            //save the new title in firebase
            if (roomId != null) {
                PushData.setRoomDescription(roomId, newTitle)
            }
            true
        }
        R.id.action_videocall -> {
            val args = Bundle()
            args.putString(Constants.ROOM_ID, roomId)
            args.putString(Constants.USER_NAME, userName)

            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtras(args)
            this.startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        //Do nothing, can only leave breakroom via button
    }
}