package com.example.virtualbreak.view.view_activitys.breakroom

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
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
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.adapters.ChatAdapter
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.Roomtype
import com.example.virtualbreak.view.view_activitys.VideoCallActivity
import com.example.virtualbreak.view.view_fragments.boredapi.BoredApiFragment
import com.example.virtualbreak.view.view_fragments.hangman.HangmanFragment
import com.example.virtualbreak.view.view_fragments.question.QuestionFragment
import com.example.virtualbreak.view.view_fragments.sportRoom.SportRoomExtrasFragment
import com.example.virtualbreak.view.view_fragments.textchat.TextchatFragment
import com.google.android.material.snackbar.Snackbar


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

    // default room type (which needs no extras)
    private var roomType: String = Roomtype.COFFEE.dbStr

    private var gameId: String? = null

    private var chatAdapter: ChatAdapter? = null

    private var activity = this

    private var activeCall = false

    /**
     * Set up the Coomunication to the Widget via a LocalBroadCastManager
     */
    lateinit var localBroadcastManager: LocalBroadcastManager
    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            Log.d(TAG, "recived message from Widget: ${intent?.action}")
            when (intent?.action) {
                BreakroomWidgetService.ACTION_LEAVE_ROOM -> leaveRoom()
                BreakroomWidgetService.ACTION_VIDEO_CALL -> videoCall()
                BreakroomWidgetService.ACTION_CHECK_USERS -> {
                    checkIfDialogIsNeededForWidget()
                }
                BreakroomWidgetService.ACTION_UNREGISTER -> localBroadcastManager.unregisterReceiver(
                    this
                )
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break_room)

        // get intent extras
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            userName = bundle.getString(Constants.USER_NAME)
            val type = bundle.getString(Constants.ROOM_TYPE)
            if (type != null) {
                roomType = type
            }
            val game = bundle.getString(Constants.GAME_ID)
            if (game != null) {
                gameId = game
            }
        }


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//disable rotate

        if (roomType.equals(Roomtype.GAME.dbStr)) {
            // add Game fragment here
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                if (gameId != null) {
                    val fragment =
                        findViewById<FragmentContainerView>(R.id.fragment_container_game_view)
                    fragment.setVisibility(View.VISIBLE)

                    // passing extras to fragment
                    val bundle = bundleOf(Constants.GAME_ID to gameId)
                    replace<HangmanFragment>(R.id.fragment_container_game_view, args = bundle)
                }

                replace<TextchatFragment>(R.id.fragment_container_chat_view)
            }
        } else if (roomType.equals(Roomtype.SPORT.dbStr)) {
            // add Sport fragment here
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                val fragment =
                    findViewById<FragmentContainerView>(R.id.fragment_container_game_view)
                fragment.setVisibility(View.VISIBLE)

                replace<SportRoomExtrasFragment>(R.id.fragment_container_game_view)
                replace<TextchatFragment>(R.id.fragment_container_chat_view)

            }
        } else if (roomType.equals(Roomtype.COFFEE.dbStr)) {
            // add Bored API fragment here
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                val fragment =
                    findViewById<FragmentContainerView>(R.id.fragment_container_game_view)
                fragment.setVisibility(View.VISIBLE)

                replace<BoredApiFragment>(R.id.fragment_container_game_view)
                replace<TextchatFragment>(R.id.fragment_container_chat_view)

            }
        } else if (roomType.equals(Roomtype.QUESTION.dbStr)) {
            // add Question fragment here
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                val fragment =
                    findViewById<FragmentContainerView>(R.id.fragment_container_game_view)
                fragment.setVisibility(View.VISIBLE)

                replace<QuestionFragment>(R.id.fragment_container_game_view)
                replace<TextchatFragment>(R.id.fragment_container_chat_view)

            }
        } else {
            // when nothing matchs, add only chat fragment
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<TextchatFragment>(R.id.fragment_container_chat_view)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }

        mtoolbar = findViewById(R.id.toolbar_breakroom)
        editText = findViewById(R.id.et_changeRoomName)

        setSupportActionBar(mtoolbar)
        //For the Back Button
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (roomId != null) {
            viewModel.loadUsersOfRoom()
            if (!SharedPrefManager.instance.getIsWidgetAllowedtoOpen()) {
                //Close Widget
                stopService(Intent(this, BreakroomWidgetService::class.java))
                SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(true)
            }
            //Allowed the Widget to open
            SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(true)
            //bug moved to other places this can lead to too many messages when device screen turns on and off
            // PushData.joinRoom(this, roomId, userName)

            viewModel.getRoom().observe(this, Observer<Room> { observedRoom ->

                room = observedRoom

                if (observedRoom != null) {
                    supportActionBar?.title = observedRoom.description

                    val callBefore = activeCall
                    // there are callMembers so it exists an active call
                    if (observedRoom.callMembers != null) {
                        activeCall = true
                        // update menu only if call has changed
                        if (callBefore != activeCall) {
                            invalidateOptionsMenu()
                        }
                    } else {
                        activeCall = false
                        // update menu only if call has changed
                        if (callBefore != activeCall) {
                            invalidateOptionsMenu()
                        }
                    }
                }
            })
        } else {
            Toast.makeText(this, R.string.something_wrong, Toast.LENGTH_LONG).show()
            // ends activity and return to previous
            finish()
        }

        //Set up the communication with the service before starting the service
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        registerBroadcastRecvicers()


    }


    /**
     * Register the BraodCastRecivers fro the Communication with the Widget
     */
    private fun registerBroadcastRecvicers() {
        localBroadcastManager.registerReceiver(
            broadCastReceiver,
            IntentFilter(BreakroomWidgetService.ACTION_LEAVE_ROOM)
        )
        localBroadcastManager.registerReceiver(
            broadCastReceiver,
            IntentFilter(BreakroomWidgetService.ACTION_VIDEO_CALL)
        )
        localBroadcastManager.registerReceiver(
            broadCastReceiver,
            IntentFilter(BreakroomWidgetService.ACTION_UNREGISTER)
        )
        localBroadcastManager.registerReceiver(
            broadCastReceiver,
            IntentFilter(BreakroomWidgetService.ACTION_CHECK_USERS)
        )
    }

    /**
     * Set the menu toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.breakroom_menu, menu)
        return true
    }

    /**
     * Update the Menu Icons
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        // when there is an active call, highlight the call item
        if (activeCall) {
            menu?.findItem(R.id.action_videocall)?.setIcon(R.drawable.videocall_white_call)
        } else {
            menu?.findItem(R.id.action_videocall)?.setIcon(R.drawable.videocall_white)
        }
        return true
    }

    /**
     * Set the Click-Events for the menu items
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        //Edit the RoomName
        R.id.action_edit -> {
            //User wants to edit the room name
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
        //Confirm the RoomName
        R.id.action_enter -> {
            val newTitle = editText.text.toString()
            editText.visibility = View.GONE
            mtoolbar.menu.findItem(R.id.action_enter).isVisible = false
            mtoolbar.menu.findItem(R.id.action_edit).isVisible = true

            //save the new title in firebase
            if (roomId != null) {
                if ("".equals(newTitle)) {
                    Snackbar.make(
                        editText,
                        "Du hast keinen neuen Namen eingegeben!",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Action", null).show()
                } else {
                    PushData.setRoomDescription(roomId, newTitle)
                    mtoolbar.title = newTitle
                }
            }

            hideSoftKeyboard(editText)
            true
        }
        // User wants to start videocall
        R.id.action_videocall -> {
            SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(false)
            videoCall()
            true
        }
        // User wants to leave room
        R.id.action_leaveRoom -> {
            checkIfDialogIsNeeded()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Check if a alert dialog if the room gets deleted it needed
     */
    private fun checkIfDialogIsNeeded() {
        if (room?.users?.size == 1) {
            showDialog()
        } else {
            leaveRoom()
        }
    }

    /**
     * Check if the AlertDiaglog if the room gets deleted is needed for the widget
     */
    fun checkIfDialogIsNeededForWidget() {
        if (room?.users?.size == 1) {
            localBroadcastManager.sendBroadcast(Intent(BreakroomWidgetService.ACTION_SHOW_ALERT))
        } else {
            leaveRoom()
        }
    }

    /**
     * Open the Widget when going Back
     */
    override fun onBackPressed() {
        super.onBackPressed()
        openWidget()
    }

    /**
     * If the Widget is not currently open and the user is in a Breakroom, open the Widget
     */
    override fun onPause() {
        super.onPause()
        if (SharedPrefManager.instance.getIsWidgetAllowedtoOpen() && SharedPrefManager.instance.getRoomId() != null) {
            openWidget()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

    private fun askPermission() {
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


    /**
     * User wants to leave room,
     * so check if it's the last user and remove user from user list of room
     * And finish the activity
     */
    private fun leaveRoom() {
        viewModel.getRoom().removeObservers(this)
        PushData.leaveRoom(this, room, userName)
        SharedPrefManager.instance.removeRoomId()

        //automatically reset status to status before INBREAK
        PushData.resetStatusToBeforeBreak()
        stopService(Intent(this, BreakroomWidgetService::class.java))
        //Forbid the Widget to open
        SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(false)

        Log.d(TAG, "Left room $roomId")
        finish()

    }

    /**
     * Opens videocall activity with current roomId and userName
     */
    private fun videoCall() {
        val args = Bundle()
        args.putString(Constants.ROOM_ID, roomId)
        args.putString(Constants.USER_NAME, userName)

        //Widget is open
        if (SharedPrefManager.instance.getWidgetVideoCallManager()) {
            stopService(Intent(this, BreakroomWidgetService::class.java))
            args.putString(Constants.ROOM_NAME, room?.description)
            args.putString(Constants.ROOM_TYPE, room?.type?.dbStr)
            args.putString(Constants.GAME_ID, gameId)
        }
        val intent = Intent(this, VideoCallActivity::class.java)
        intent.putExtras(args)
        this.startActivity(intent)
    }

    /**
     * Asks the user if he really wants to continue leaving the room
     */
    private fun showDialog() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.breakroom_alert_dialog)
        val yesBtn = dialog.findViewById(R.id.room_alert_confirm_button) as Button
        val noBtn = dialog.findViewById(R.id.room_alert_dismiss_button) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
            leaveRoom()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    /**
     * Opens an overlaay which indicates that user is in a room with all necessary intent extras
     */
    private fun openWidget() {
        Log.d(TAG, "openwidget")
        if (Settings.canDrawOverlays(this)) {
            if (SharedPrefManager.instance.getIsWidgetAllowedtoOpen()) {
                //if(!SharedPrefManager.instance.getIsWidgetOpen()) {
                SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(false)
                val intent = Intent(this, BreakroomWidgetService::class.java)
                intent.putExtra(Constants.ROOM_NAME, room?.description)
                intent.putExtra(Constants.ROOM_TYPE, room?.type?.dbStr)
                intent.putExtra(Constants.USER_NAME, userName)
                intent.putExtra(Constants.GAME_ID, gameId)
                startService(intent)
                finish()
            }
            //}
        } else {
            askPermission()
            Toast.makeText(
                this,
                "You need System Alert Window Permission to do this",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    /**
     * closes soft keyboard
     * editText: View
     */
    private fun hideSoftKeyboard(editText: EditText) {
        val imm: InputMethodManager? =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.getWindowToken(), 0)
    }
}