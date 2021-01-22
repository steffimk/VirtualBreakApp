package com.example.virtualbreak.view.view_activitys.breakroom

import android.R.attr.data
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.virtualbreak.R
import kotlin.math.abs


class BreakroomWidgetService : Service() {

    private val TAG = "BreakRoomWidgetService"

    private lateinit var mWindowManager: WindowManager
    private lateinit var mFloatingView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private var roomName: String? = "RoomName"
    private var roomType: String? = "RoomType"

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

    private var isShowing = false
    private var touchConsumedByMove = false

    private val onTouchListener = View.OnTouchListener { view, event ->
        val totalDeltaX = lastX - firstX
        val totalDeltaY = lastY - firstY

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("BreakroomWidget", "ACTION_DOWN")
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
                firstX = lastX
                firstY = lastY
            }
            MotionEvent.ACTION_UP -> {
                view.performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("BreakroomWidget", "ACTION_MOVE")
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        roomName = intent?.extras!!["RoomName"] as String?
        roomType = intent?.extras!!["RoomType"] as String?
        Log.d("BreakRoom", roomName + roomType)
        return super.onStartCommand(intent, flags, startId)
    }

}