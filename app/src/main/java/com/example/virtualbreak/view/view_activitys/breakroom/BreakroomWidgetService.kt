package com.example.virtualbreak.view.view_activitys.breakroom

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager


class BreakroomWidgetService : Service() {

    private val TAG = "BreakRoomWidgetService"

    private lateinit var mFloatingView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private lateinit var altertView: View
    private lateinit var buttonView: View
    private var roomName = "RoomName"
    private var roomType = "RoomType"
    private var userName = "UserName"
    private var gameId: String? = null

    private lateinit var mWindowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams

    private val channelID = "widget_channel"

    /**
     * Set up the Communication with the Activity via a localBroadcastManager
     */
    lateinit var localBroadcastManager: LocalBroadcastManager
    val widgetBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_SHOW_ALERT -> {
                    buttonView.visibility = View.GONE
                    altertView.visibility = View.VISIBLE
                }
            }
        }
    }

    //Don't bind the Service to a activity
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle: Bundle? = intent?.extras
        if (bundle != null) {
            // save other constants
            roomName = bundle.getString(Constants.ROOM_NAME).toString()
            userName = bundle.getString(Constants.USER_NAME).toString()
            roomType = bundle.getString(Constants.ROOM_TYPE).toString()
            gameId = bundle.getString(Constants.GAME_ID).toString()
            //Set the View up with all the Information
            mFloatingView.findViewById<TextView>(R.id.widget_roomName_textview).text = roomName
            mFloatingView.findViewById<TextView>(R.id.widget_roomtype_textview).text = roomType

        }

        //Open a notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = channelID
            val description = getString(R.string.notification_Channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = description

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }

        //Add the intent for the Click on the Notification
        val notificationIntent = Intent(
            this@BreakroomWidgetService,
            BreakRoomActivity::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra(Constants.USER_NAME, userName)
        notificationIntent.putExtra(Constants.ROOM_TYPE, roomType)
        notificationIntent.putExtra(Constants.GAME_ID, gameId)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        //Set the Notification for the Foreground Service
        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_hint) + " " + roomName)
            .setSmallIcon(R.drawable.ic_vb_alt)
            .setContentIntent(pendingIntent)
            .build()

        //Start the foreground service
        startForeground(1, notification)
        return START_STICKY;

    }


    override fun onCreate() {
        super.onCreate()
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

        //Specify parameters for the layout
        params = WindowManager.LayoutParams().apply {
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            @Suppress("DEPRECATION")
            type = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else -> WindowManager.LayoutParams.TYPE_TOAST
            }

            //specify the position and height of the floating widget
            gravity = Gravity.CENTER
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        //getting windows services and adding the floating view to it
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mFloatingView, params)

        //getting the collapsed and expanded view as well as other View components from the floating view
        collapsedView = mFloatingView.findViewById(R.id.widget_layoutCollapsed)
        expandedView = mFloatingView.findViewById(R.id.widget_layoutExpanded)
        altertView = mFloatingView.findViewById(R.id.widget_leave_alert)
        buttonView = mFloatingView.findViewById(R.id.widget_button_layout)

        //Set the OntouchListener for the rootParent
        val layoutParent = mFloatingView.findViewById<View>(R.id.widget_relativeLayoutParent)
        layoutParent.setOnTouchListener(onTouchListener)


        //Set all the onClicklisteners for the Buttons
        //Expand widget
        mFloatingView.findViewById<ImageButton>(R.id.widget_expanded_iv).setOnClickListener {
            Log.d(TAG, "click expand")
            collapsedView.visibility = View.GONE
            expandedView.visibility = View.VISIBLE
        }

        //Collapse widget
        mFloatingView.findViewById<ImageButton>(R.id.widget_minimize_iv).setOnClickListener {
            Log.d(TAG, "click minimize")
            expandedView.animate().alpha(0.0f)
            expandedView.visibility = View.GONE
            collapsedView.animate().alpha(1.0f)
            collapsedView.visibility = View.VISIBLE
        }

        //LeaveRoomButton
        mFloatingView.findViewById<ImageButton>(R.id.widget_button_leaveroom).setOnClickListener {
            Log.d(TAG, "Leave room")
            localBroadcastManager.sendBroadcast(Intent(ACTION_CHECK_USERS))
        }

        //VideoCallButton
        mFloatingView.findViewById<ImageButton>(R.id.widget_button_videocall).setOnClickListener {
            Log.d(TAG, "Join Video call")
            SharedPrefManager.instance.saveWidgetVideoCallManager(true)
            localBroadcastManager.sendBroadcast(Intent(ACTION_VIDEO_CALL))
        }

        //Open Room Button
        mFloatingView.findViewById<Button>(R.id.widget_button_open_room).setOnClickListener {
            openRoom()
        }

        //Yes and no button for alert when Room gets deleted
        mFloatingView.findViewById<Button>(R.id.widget_button_leave_yes).setOnClickListener {
            localBroadcastManager.sendBroadcast(Intent(ACTION_LEAVE_ROOM))
        }
        mFloatingView.findViewById<Button>(R.id.widget_button_leave_no).setOnClickListener {
            altertView.visibility = View.GONE
            buttonView.visibility = View.VISIBLE
        }

    }

    /**
     * When the Service gets destroyed, allowed widget to open, unregister the boradcastRecivers and stop Service
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Destroy Widget Service")
        SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(true)
        localBroadcastManager.sendBroadcast(Intent(ACTION_UNREGISTER))
        localBroadcastManager.unregisterReceiver(widgetBroadCastReceiver)
        mWindowManager.removeView(mFloatingView)
        stopForeground(true)
        stopSelf()
    }

    /**
     * Open the current Breakroomactivity
     */
    private fun openRoom() {
        val intent = Intent(this@BreakroomWidgetService, BreakRoomActivity::class.java)
        intent.putExtra(Constants.USER_NAME, userName)
        intent.putExtra(Constants.ROOM_TYPE, roomType)
        intent.putExtra(Constants.GAME_ID, gameId)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        stopSelf()
    }


    /**
     * The OnTouchListener for the Widget to move around
     */

    //initial x and y coordinates
    private var initialX = 0
    private var initialY = 0

    //new touch x and y coordinates
    private var touchX = 0f
    private var touchY = 0f


    private val onTouchListener = View.OnTouchListener { view, event ->

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                //Get the current Touch Location and save it
                initialX = params.x;
                initialY = params.y;
                //get the current Touch location
                touchX = event.rawX;
                touchY = event.rawY;
                return@OnTouchListener true
            }
            MotionEvent.ACTION_UP -> {
                view.performClick()
                return@OnTouchListener true
            }
            MotionEvent.ACTION_MOVE -> {
                //Calculate the new x and y value and update the Widget on every move
                params.x = initialX + (event.rawX - touchX).toInt();
                params.y = initialY + (event.rawY - touchY).toInt();
                mWindowManager.updateViewLayout(mFloatingView, params);
                return@OnTouchListener true
            }
        }
        return@OnTouchListener false
    }


    /**
     * ALL Communicatation Constatns for the localBroadCastManager
     */
    companion object {
        const val ACTION_VIDEO_CALL = "videocall"
        const val ACTION_LEAVE_ROOM = "leave_room"
        const val ACTION_UNREGISTER = "unregister"
        const val ACTION_CHECK_USERS = "checkUSers"
        const val ACTION_SHOW_ALERT = "showAlter"
    }

}
