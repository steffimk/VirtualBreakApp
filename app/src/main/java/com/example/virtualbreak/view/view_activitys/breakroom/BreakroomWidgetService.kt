package com.example.virtualbreak.view.view_activitys.breakroom

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import kotlin.math.abs


class BreakroomWidgetService : Service() {

    private val TAG = "BreakRoomWidgetService"

    private lateinit var mWindowManager: WindowManager
    private lateinit var mFloatingView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private lateinit var altertView: View
    private lateinit var buttonView: View
    private var roomName = "RoomName"
    private var roomType = "RoomType"
    private var userName = "UserName"

    private var gameId: String? = null

    private lateinit var params: WindowManager.LayoutParams
    lateinit var localBroadcastManager: LocalBroadcastManager
    val widgetBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            Log.d("CHECk", "recivedmessage ${intent?.action}")
            when (intent?.action) {
                ACTION_SHOW_ALERT -> {
                    buttonView.visibility = View.GONE
                    altertView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle: Bundle? = intent?.extras
        if (bundle != null) {
            mFloatingView.findViewById<TextView>(R.id.widget_roomName_textview).text =
                bundle.getString(Constants.ROOM_NAME).toString()
            mFloatingView.findViewById<TextView>(R.id.widget_roomtype_textview).text =
                "Pausentyp  ${bundle.getString(Constants.ROOM_TYPE).toString()}"
            userName = bundle.getString(Constants.USER_NAME).toString()
            roomType = bundle.getString(Constants.ROOM_TYPE).toString()
            gameId = bundle.getString(Constants.GAME_ID).toString()
            Log.d("BreakRoom3", roomName + roomType)
        }
//        return super.onStartCommand(intent, flags, startId)
//        startForegroundService()
        return START_NOT_STICKY;
    }


    override fun onCreate() {
        super.onCreate()
        Log.d("Check", "onCreatewidget " + SharedPrefManager.instance.getIsWidgetAllowedtoOpen())
        setTheme(R.style.Theme_VirtualBreak)

        //Inflate the Layout
        mFloatingView = LayoutInflater.from(this).inflate(
            R.layout.floating_widget_layout,
            null
        )

        //register the coomunication to the Activity
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(
            widgetBroadCastReceiver,
            IntentFilter(ACTION_SHOW_ALERT)
        )

        //Spefify parameters for the layout
        params = WindowManager.LayoutParams().apply {
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            @Suppress("DEPRECATION")
            type = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else -> WindowManager.LayoutParams.TYPE_TOAST
            }

            gravity = Gravity.CENTER
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        //getting windows services and adding the floating view to it
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mFloatingView, params)

        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.widget_layoutCollapsed)
        expandedView = mFloatingView.findViewById(R.id.widget_layoutExpanded)
        altertView = mFloatingView.findViewById(R.id.widget_leave_alert)
        buttonView = mFloatingView.findViewById(R.id.widget_button_layout)

        //Set the OntouchListener for the rootParent
        val layoutParent = mFloatingView.findViewById<View>(R.id.widget_relativeLayoutParent)
        layoutParent.setOnTouchListener(onTouchListener)


        mFloatingView.findViewById<TextView>(R.id.widget_roomName_textview).text = roomName
        mFloatingView.findViewById<TextView>(R.id.widget_roomtype_textview).text = roomType

        mFloatingView.findViewById<ImageButton>(R.id.widget_expanded_iv).setOnClickListener {
            Log.d(TAG, "click expand")
            collapsedView.visibility = View.GONE
            expandedView.visibility = View.VISIBLE
        }

        mFloatingView.findViewById<ImageButton>(R.id.widget_minimize_iv).setOnClickListener {
            Log.d(TAG, "click minimize")
            collapsedView.visibility = View.VISIBLE
            expandedView.visibility = View.GONE
        }

        mFloatingView.findViewById<ImageButton>(R.id.widget_button_leaveroom).setOnClickListener {
            Log.d(TAG, "Leave room")
            localBroadcastManager.sendBroadcast(Intent(ACTION_CHECK_USERS))
        }

        mFloatingView.findViewById<ImageButton>(R.id.widget_button_videocall).setOnClickListener {
            Log.d(TAG, "Join Video call")
            SharedPrefManager.instance.saveWidgetVideoCallManager(true)
            localBroadcastManager.sendBroadcast(Intent(ACTION_VIDEO_CALL))
        }

        mFloatingView.findViewById<Button>(R.id.widget_button_open_room).setOnClickListener {
            openRoom()
        }

        mFloatingView.findViewById<Button>(R.id.widget_button_leave_yes).setOnClickListener {
            localBroadcastManager.sendBroadcast(Intent(ACTION_LEAVE_ROOM))
        }
        mFloatingView.findViewById<Button>(R.id.widget_button_leave_no).setOnClickListener {
            altertView.visibility = View.GONE
            buttonView.visibility = View.VISIBLE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "OnDestroy")
        SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(true)
        localBroadcastManager.sendBroadcast(Intent(ACTION_UNREGISTER))
        localBroadcastManager.unregisterReceiver(widgetBroadCastReceiver)
        Log.d("Check", "onDestroyWidget" + SharedPrefManager.instance.getIsWidgetAllowedtoOpen())
        mWindowManager.removeView(mFloatingView)
        stopSelf()
    }

    private fun openRoom() {
        val intent = Intent(this@BreakroomWidgetService, BreakRoomActivity::class.java)
        intent.putExtra(Constants.USER_NAME, userName)
        intent.putExtra(Constants.ROOM_TYPE, roomType)
        intent.putExtra(Constants.GAME_ID, gameId)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        //close the service and remove view from the view hierarchy
        stopSelf()
    }


    //OnTouchListener
    private var lastX: Int = 0
    private var lastY: Int = 0
    private var firstX: Int = 0
    private var firstY: Int = 0

    private var touchConsumedByMove = false

    private val onTouchListener = View.OnTouchListener { view, event ->
        val totalDeltaX = lastX - firstX
        val totalDeltaY = lastY - firstY

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
                firstX = lastX
                firstY = lastY
            }
            MotionEvent.ACTION_UP -> {
                view.performClick()
                ///Activate for View Change, not realy good
                // handleViewChange()

            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.rawX.toInt() - lastX
                val deltaY = event.rawY.toInt() - lastY
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
                if (abs(totalDeltaX) >= 5 || abs(totalDeltaY) >= 5) {
                    if (event.pointerCount == 1) {
                        params.x += deltaX
                        params.y += deltaY
                        touchConsumedByMove = true
                        mWindowManager?.apply {
                            updateViewLayout(mFloatingView, params)
                        }
                    } else {
                        touchConsumedByMove = false
                    }
                } else {
                    touchConsumedByMove = false
                }
            }
            else -> {
            }
        }
        touchConsumedByMove
    }



    companion object {
        const val ACTION_VIDEO_CALL = "videocall"
        const val ACTION_LEAVE_ROOM = "leave_room"
        const val ACTION_UNREGISTER = "unregister"
        const val ACTION_CHECK_USERS = "checkUSers"
        const val ACTION_SHOW_ALERT = "showAlter"
    }

}
