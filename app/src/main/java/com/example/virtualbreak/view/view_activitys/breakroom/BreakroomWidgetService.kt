package com.example.virtualbreak.view.view_activitys.breakroom

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.virtualbreak.R
import com.example.virtualbreak.view.view_activitys.MainActivity


class BreakroomWidgetService : Service(), View.OnClickListener {

    private val TAG = "BreakRoomWidgetService"

    private lateinit var mWindowManager: WindowManager
    private lateinit var mFloatingView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private lateinit var logo: RelativeLayout


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        setTheme(R.style.Theme_VirtualBreak)

        mFloatingView = LayoutInflater.from(this).inflate(
            R.layout.floating_widget_layout,
            null
        )

        val LAYOUT_FLAG: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP
        params.x = 10
        params.y = 100

        //getting windows services and adding the floating view to it
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mFloatingView, params)


        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.widget_layoutCollapsed)
        expandedView = mFloatingView.findViewById(R.id.widget_layoutExpanded)

        //adding click listener to close button and expanded view

        //adding click listener to close button and expanded view
        //mFloatingView.setOnClickListener(this)
        expandedView.findViewById<View>(R.id.widget_logo_expanded).setOnClickListener(this)

        var leaveRoomButton: ImageView = mFloatingView.findViewById(R.id.widget_button_leaveroom)
        leaveRoomButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@BreakroomWidgetService, "LeaveRoom.", Toast.LENGTH_LONG)
                    .show()
            }
        })

//        val openRoomButton: Button = mFloatingView.findViewById(R.id.widget_button_open_room)
//        openRoomButton.setOnClickListener {
//            val intent = Intent(this@FloatingViewService, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//            //close the service and remove view from the view hierarchy
//            stopSelf()
//        }
//        expandedView.findViewById<View>(R.id.widget_button_open_room).setOnClickListener(this)
//        expandedView.findViewById<View>(R.id.widget_button_videocall).setOnClickListener(this)

        //logo = mFloatingView.findViewById(R.id.relativeLayoutParent)

//        mFloatingView.findViewById<View>(R.id.relativeLayoutParent).setOnTouchListener(object : OnTouchListener{
//            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
//
//                //remember the initial position.
//                initialX = params.x;
//                initialY = params.y;
//
//
//                //get the touch location
//                initialTouchX = event.getRawX();
//                initialTouchY = event.getRawY();
//                return true;
//                case MotionEvent.ACTION_MOVE:
//                //Calculate the X and Y coordinates of the view.
//                params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                params.y = initialY + (int) (event.getRawY() - initialTouchY);
//
//
//                //Update the layout with new X & Y coordinate
//                mWindowManager.updateViewLayout(mFloatingView, params);
//                return true;
//            }
//            return false;
//            }
//
//        })


        mFloatingView.findViewById<ImageView>(R.id.widget_collapsed_iv).setOnTouchListener(object :
            OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                v?.performClick()
                Log.d(TAG, "OnTouch" + event.action)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y


                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        return true
                    }
                    MotionEvent.ACTION_UP ->
                        //Add code for launching application and positioning the widget to nearest edge.
                        return true
                    MotionEvent.ACTION_MOVE -> {
                        val Xdiff = Math.round(event.rawX - initialTouchX).toFloat()
                        val Ydiff = Math.round(event.rawY - initialTouchY).toFloat()


                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + Xdiff.toInt()
                        params.y = initialY + Ydiff.toInt()

                        //Update the layout with new X & Y coordinates
                        mWindowManager.updateViewLayout(mFloatingView, params)
                        return true
                    }
                }
                return false
            }
        })


//        mFloatingView.findViewById<View>(R.id.relativeLayoutParent)
//            .setOnTouchListener(object : OnTouchListener {
//                private var initialX = 0
//                private var initialY = 0
//                private var initialTouchX = 0f
//                private var initialTouchY = 0f
//                override fun onTouch(v: View, event: MotionEvent): Boolean {
//                    when (event.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            initialX = params.x
//                            initialY = params.y
//                            initialTouchX = event.rawX
//                            initialTouchY = event.rawY
//                            return true
//                        }
//                        MotionEvent.ACTION_UP -> {
//                            //when the drag is ended switching the state of the widget
//                            collapsedView.visibility = View.GONE
//                            expandedView.visibility = View.VISIBLE
//                            return true
//                        }
//                        MotionEvent.ACTION_MOVE -> {
//                            //this code is helping the widget to move around the screen with fingers
//                            params.x = initialX + (event.rawX - initialTouchX).toInt()
//                            params.y = initialY + (event.rawY - initialTouchY).toInt()
//                            mWindowManager.updateViewLayout(mFloatingView, params)
//                            return true
//                        }
//                    }
//                    return false
//                }
//            })


    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null) {
            mWindowManager.removeView(mFloatingView)
        }

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