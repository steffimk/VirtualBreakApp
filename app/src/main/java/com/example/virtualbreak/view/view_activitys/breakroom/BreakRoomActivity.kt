package com.example.virtualbreak.view.view_activitys.breakroom

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
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
import kotlinx.android.synthetic.main.activity_break_room.*


class BreakRoomActivity : AppCompatActivity() {

    private val TAG = "BreakRoomActivity"
    private val DRAW_OVER_OTHER_APP_PERMISSION = 123

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

    private var activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break_room)

        //val roomId = SharedPrefManager.instance.getRoomId()
        //var userName : String? = null

        //Close widget, not widget in breakroomact

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }



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

            var layoutManager = LinearLayoutManager(this)
            layoutManager.setStackFromEnd(true)
            chat_messages_recycler_view.layoutManager = layoutManager

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
            if (!message.isEmpty()) {
                if (roomId != null) {
                    PushData.sendMessage(roomId, message)
                }
            } else {
                Toast.makeText(
                    this, R.string.toast_enter_message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            input.clear()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.breakroom_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        android.R.id.home -> {
            // when leaving a room remove the roomId from preferences because it's not needed anymore and ends this activity
            //leaveRoom()
            openWidget()
            Log.d(TAG, "Back/home pressed")
            true
        }

        R.id.action_edit -> {
            //User wants to edit the room name
            supportActionBar?.title = null
            editText.visibility = View.VISIBLE
            //Show the keyboard
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            editText.requestFocus()

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
            videocall()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        //leaveRoom()
        openWidget()
    }

    override fun onPause() {
        super.onPause()


        // To prevent starting the service if the required permission is NOT granted.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
            startService(
                Intent(
                    this@BreakRoomActivity,
                    BreakroomWidgetService::class.java
                ).putExtra("activity_background", true)
            )
            finish()
        } else {
            Toast.makeText(
                this,
                "You need System Alert Window Permission to do this",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onACtivityREsult")
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    //Permission is not available. Display error text.
                    Toast.makeText(
                        this,
                        "You need System Alert Window Permission to do this",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun leaveRoom() {
        if (room?.users?.size == 1) {
            showDialog()
        } else {
            viewModel.getRoom().removeObservers(this)
            PushData.leaveRoom(this, room, userName)
            SharedPrefManager.instance.removeRoomId()
            Log.d(TAG, "Left room $roomId")
            finish()
        }
    }

    fun videocall() {
        val args = Bundle()
        args.putString(Constants.ROOM_ID, roomId)
        args.putString(Constants.USER_NAME, userName)

        val intent = Intent(this, VideoCallActivity::class.java)
        intent.putExtras(args)
        this.startActivity(intent)
    }

    private fun showDialog() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.breakroom_alert_dialog)
//        val body = dialog.findViewById(R.id.b) as TextView
//        body.text = title
        val yesBtn = dialog.findViewById(R.id.room_alert_confirm_button) as Button
        val noBtn = dialog.findViewById(R.id.room_alert_dismiss_button) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
            viewModel.getRoom().removeObservers(this)
            PushData.leaveRoom(this, room, userName)
            SharedPrefManager.instance.removeRoomId()
            Log.d(TAG, "Left room $roomId")
            finish()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

    private fun openWidget() {
        Log.d(TAG, "openwidget")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this@BreakRoomActivity)) {
            Log.d("BreakRoom", room?.description + room?.type?.dbStr)
            val intent: Intent = Intent(this, BreakroomWidgetService::class.java)
            intent.putExtra("RoomName", room?.description)
            intent.putExtra("RoomType", room?.type?.dbStr)
            startService(intent)
            finish()
        } else if (Settings.canDrawOverlays(this)) {
            Log.d("BreakRoom", room?.description + room?.type?.dbStr)
            val intent: Intent = Intent(this, BreakroomWidgetService::class.java)
            intent.putExtra("RoomName", room?.description)
            intent.putExtra("RoomType", room?.type?.dbStr)
            startService(intent)
            finish()
        } else {
            askPermission()
            Toast.makeText(
                this,
                "You need System Alert Window Permission to do this",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun askPermission() {
//        val intent = Intent(
//            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//            Uri.parse("package:$packageName")
//        )
//        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION)
//    }

        Log.d(TAG, "askPermission")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
            );
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }
}