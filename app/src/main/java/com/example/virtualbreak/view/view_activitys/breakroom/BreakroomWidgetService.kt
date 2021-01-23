package com.example.virtualbreak.view.view_activitys.breakroom

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import kotlin.math.abs


class BreakroomWidgetService : Service() {

    private val TAG = "BreakRoomWidgetService"

    private lateinit var mWindowManager: WindowManager
    private lateinit var mFloatingView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private var roomName = "RoomName"
    private var roomType = "RoomType"
    private var userName = "UserName"

    private lateinit var params: WindowManager.LayoutParams


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        setTheme(R.style.Theme_VirtualBreak)

        //Inflate the Layout
        mFloatingView = LayoutInflater.from(this).inflate(
            R.layout.floating_widget_layout,
            null
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


//        var testClose: ImageView = mFloatingView.findViewById(R.id.widget_test_close)
//        testClose.setOnClickListener {
//            stopSelf()
//        }

        //Set the OntouchListener for the rootParent
        val layoutParent = mFloatingView.findViewById<View>(R.id.widget_relativeLayoutParent)
        layoutParent.setOnTouchListener(onTouchListener)


        mFloatingView.findViewById<TextView>(R.id.widget_roomName_textview).text = roomName
        mFloatingView.findViewById<TextView>(R.id.widget_roomtype_textview).text = roomType


        val leaveRoomButton: ImageButton = mFloatingView.findViewById(R.id.widget_button_leaveroom)
        leaveRoomButton.setOnClickListener {
            Log.d(TAG, "Leave room")
        }

        val videoCall: ImageButton = mFloatingView.findViewById(R.id.widget_button_videocall)
        videoCall.setOnClickListener {
            Log.d(TAG, "Join Video call")
        }

        val openRoomButton: Button = mFloatingView.findViewById(R.id.widget_button_open_room)
        openRoomButton.setOnClickListener {
            Log.d(TAG, "Open room")
            val intent = Intent(this@BreakroomWidgetService, BreakRoomActivity::class.java)
            intent.putExtra(Constants.USER_NAME, userName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            //close the service and remove view from the view hierarchy
            stopSelf()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager.removeView(mFloatingView)
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

    private fun handleViewChange() {

        if (mFloatingView == null || mFloatingView.findViewById<View>(R.id.widget_layoutCollapsed).visibility == View.VISIBLE) {
            //When user clicks on the image view of the collapsed layout,
            //visibility of the collapsed layout will be changed to "View.GONE"
            //and expanded view will become visible.
            collapsedView.visibility = View.GONE
            expandedView.visibility = View.VISIBLE
        } else {
            collapsedView.visibility = View.VISIBLE
            expandedView.visibility = View.GONE
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle: Bundle? = intent?.extras
        if (bundle != null) {
            mFloatingView.findViewById<TextView>(R.id.widget_roomName_textview).text =
                bundle.getString(Constants.ROOM_NAME).toString()
            mFloatingView.findViewById<TextView>(R.id.widget_roomtype_textview).text =
                "Pausentyp  ${bundle.getString(Constants.ROOM_TYPE).toString()}"
            userName = bundle.getString(Constants.USER_NAME).toString()
            Log.d("BreakRoom3", roomName + roomType)
        }
        return super.onStartCommand(intent, flags, startId)
    }

}