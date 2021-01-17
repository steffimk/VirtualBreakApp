package com.example.virtualbreak.controller.adapters

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.View
import android.view.WindowManager

class BreakroomWidgetService : Service, View.OnClickListener {


    private lateinit var mWindowManager: WindowManager
    private lateinit var mFloatingView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }


}