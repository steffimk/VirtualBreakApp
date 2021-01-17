package com.example.virtualbreak.view.view_activitys.breakroom

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import com.example.virtualbreak.R

class BreakroomWidgetService : Service(), View.OnClickListener {

    private lateinit var mWindowManager: WindowManager
    private lateinit var mFloatingView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        mFloatingView = LayoutInflater.from(this).inflate(
            R.layout.floating_widget_layout,
            null
        )

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )


        //getting windows services and adding the floating view to it
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mFloatingView, params)


        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.widget_collapsed_iv)
        expandedView = mFloatingView.findViewById(R.id.widget_expanded_iv)

        //adding click listener to close button and expanded view

        //adding click listener to close button and expanded view
        //mFloatingView.setOnClickListener(this)
        expandedView.findViewById<View>(R.id.widget_logo_expanded).setOnClickListener(this)

        expandedView.findViewById<View>(R.id.widget_button_leaveroom).setOnClickListener(this)
        expandedView.findViewById<View>(R.id.widget_button_open_room).setOnClickListener(this)
        expandedView.findViewById<View>(R.id.widget_button_videocall).setOnClickListener(this)


        mFloatingView.findViewById<View>(R.id.relativeLayoutParent)
            .setOnTouchListener(object : OnTouchListener {
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            //when the drag is ended switching the state of the widget
                            collapsedView.visibility = View.GONE
                            expandedView.visibility = View.VISIBLE
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            //this code is helping the widget to move around the screen with fingers
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()
                            mWindowManager.updateViewLayout(mFloatingView, params)
                            return true
                        }
                    }
                    return false
                }
            })


    }

    override fun onDestroy() {
        super.onDestroy()

        mWindowManager.removeView(mFloatingView)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.widget_logo_expanded -> {
                //switching views
                collapsedView.visibility = View.VISIBLE
                expandedView.visibility = View.GONE
            }
            R.id.widget_button_open_room -> {
//                val notificationIntent = Intent(context, BreakRoomActivity::class.java)
//                notificationIntent.action = Intent.ACTION_MAIN
//                notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

//                val intent = Intent(context, BreakRoomActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // You need this if starting
//                //  the activity from a service
//                intent.action = Intent.ACTION_MAIN
//                intent.addCategory(Intent.CATEGORY_LAUNCHER)
//                startActivity(intent)

            }
            R.id.widget_button_leaveroom -> {

            }
            R.id.widget_button_videocall -> {

            }
            // close widget stopSelf()
        }
    }

}