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

    //set default room to room which needs no extras
    private var roomType : String = Roomtype.COFFEE.dbStr

    private var gameId: String? = null

    private var chatAdapter: ChatAdapter? = null

    private var activity = this

    private var activeCall = false

    lateinit var localBroadcastManager: LocalBroadcastManager
    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            Log.d("CHECk", "recivedmessage ${intent?.action}")
            when (intent?.action) {
                BreakroomWidgetService.ACTION_LEAVE_ROOM -> leaveRoom()
                BreakroomWidgetService.ACTION_VIDEO_CALL -> videoCall()
                BreakroomWidgetService.ACTION_CHECK_USERS -> {
                    Log.d("CHECK", "ACTION_CHECK_USERS")
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

        Log.d("Check", "onCreate " + SharedPrefManager.instance.getRoomId())

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

        //if (savedInstanceState == null) {
            if(roomType.equals(Roomtype.GAME.dbStr)){
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    if (gameId != null) {
                        val fragment =
                            findViewById<FragmentContainerView>(R.id.fragment_container_game_view)
                        fragment.setVisibility(View.VISIBLE)

                        val bundle = bundleOf(Constants.GAME_ID to gameId)

                        replace<HangmanFragment>(R.id.fragment_container_game_view, args = bundle)
                        //add<HangmanFragment>(R.id.fragment_container_game_view, args = bundle)
                    }

                    replace<TextchatFragment>(R.id.fragment_container_chat_view)
                    //add<TextchatFragment>(R.id.fragment_container_chat_view)
                }
            } else if(roomType.equals(Roomtype.SPORT.dbStr)){
                supportFragmentManager.commit {
                    setReorderingAllowed(true)

                    // makes fragment visible
                    val fragment =
                        findViewById<FragmentContainerView>(R.id.fragment_container_game_view)
                    fragment.setVisibility(View.VISIBLE)

                    // if you don't need to pass info to fragment
                    replace<SportRoomExtrasFragment>(R.id.fragment_container_game_view)
                    replace<TextchatFragment>(R.id.fragment_container_chat_view)

                }
            } else if(roomType.equals(Roomtype.COFFEE.dbStr)){
                // add Bored API fragment here
                supportFragmentManager.commit {
                    setReorderingAllowed(true)

                    // makes fragment visible
                    val fragment =
                        findViewById<FragmentContainerView>(R.id.fragment_container_game_view)
                    fragment.setVisibility(View.VISIBLE)

                    // if you don't need to pass info to fragment
                    replace<BoredApiFragment>(R.id.fragment_container_game_view)
                    replace<TextchatFragment>(R.id.fragment_container_chat_view)

                }
            } else if (roomType.equals(Roomtype.QUESTION.dbStr)){
                supportFragmentManager.commit {
                    setReorderingAllowed(true)

                    // makes fragment visible
                    val fragment =
                        findViewById<FragmentContainerView>(R.id.fragment_container_game_view)
                    fragment.setVisibility(View.VISIBLE)

                    // if you don't need to pass info to fragment
                    replace<QuestionFragment>(R.id.fragment_container_game_view)
                    replace<TextchatFragment>(R.id.fragment_container_chat_view)

                }
            }

            else{
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<TextchatFragment>(R.id.fragment_container_chat_view)
                    //add<TextchatFragment>(R.id.fragment_container_chat_view)
                }
            }
        //}



        //val roomId = SharedPrefManager.instance.getRoomId()
        //var userName : String? = null

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
            //Allowed the Widget to open
            SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(true)
            Log.d("Check", "onCreate" + SharedPrefManager.instance.getIsWidgetAllowedtoOpen())

            //bug moved to other places this can lead to too many messages when device screen turns on and off
            // PushData.joinRoom(this, roomId, userName)

            viewModel.getRoom().observe(this, Observer<Room> { observedRoom ->

                room = observedRoom
                //viewModel.loadUsersOfRoom()

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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.breakroom_menu, menu)
        return true
    }

    // updating menu
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        // when there is an active call, highlight the call item
        if(activeCall){
            menu?.findItem(R.id.action_videocall)?.setIcon(R.drawable.videocall_white_call)
        } else{
            menu?.findItem(R.id.action_videocall)?.setIcon(R.drawable.videocall_white)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//        For the Back Button
//        android.R.id.home -> {
//            // when leaving a room remove the roomId from preferences because it's not needed anymore and ends this activity
//            //leaveRoom()
//            openWidget()
//            Log.d(TAG, "Back/home pressed")
//            true
//        }

        R.id.action_edit -> {
            //User wants to edit the room name
            //supportActionBar?.title = null // uncommented because otherwise old title not shown, if new text was empty
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
        R.id.action_videocall -> {
            SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(false)
            videoCall()
            true
        }
        R.id.action_leaveRoom -> {
            checkIfDialogIsNeeded()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun checkIfDialogIsNeeded() {
        if (room?.users?.size == 1) {
            showDialog()
        } else {
            leaveRoom()
        }
    }

    fun checkIfDialogIsNeededForWidget() {
        Log.d("CHECK", "checkWidetDialog: ${room?.users?.size}")
        if (room?.users?.size == 1) {
            localBroadcastManager.sendBroadcast(Intent(BreakroomWidgetService.ACTION_SHOW_ALERT))
        } else {
            leaveRoom()
        }
    }

    override fun onBackPressed() {
        Log.d("Check", "onBacll" + SharedPrefManager.instance.getIsWidgetAllowedtoOpen())
        openWidget()
    }

    override fun onPause() {
        Log.d("Check", "onPause " + SharedPrefManager.instance.getIsWidgetAllowedtoOpen())
        super.onPause()
        if (SharedPrefManager.instance.getIsWidgetAllowedtoOpen() && SharedPrefManager.instance.getRoomId() != null) {
            openWidget()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("Check", "onStop " + SharedPrefManager.instance.getIsWidgetAllowedtoOpen())
    }

    override fun onStart() {
        super.onStart()
        Log.d("Check", "onStart " + SharedPrefManager.instance.getIsWidgetAllowedtoOpen())
    }

    override fun onResume() {
        super.onResume()
        Log.d("Check", "onResume " + SharedPrefManager.instance.getIsWidgetAllowedtoOpen())
    }

    override fun onDestroy() {
        super.onDestroy()
        //localBroadcastManager.unregisterReceiver(broadCastReceiver)
        Log.d("Check", "onDesroy ${!SharedPrefManager.instance.getIsWidgetAllowedtoOpen()}")
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

    private fun openWidget() {
        Log.d(TAG, "openwidget")
        if (Settings.canDrawOverlays(this)) {
            if (SharedPrefManager.instance.getIsWidgetAllowedtoOpen()) {
                //if(!SharedPrefManager.instance.getIsWidgetOpen()) {
                Log.d(TAG, room?.description + room?.type?.dbStr)
                SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(false)
                Log.d("Check", "openWidget" + SharedPrefManager.instance.getIsWidgetAllowedtoOpen())
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


    private fun askPermission() {
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

    /**
     * closes soft keyboard
     * editText: View
     */
    private fun hideSoftKeyboard(editText: EditText) {
        val imm: InputMethodManager? = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.getWindowToken(), 0)
    }
}
