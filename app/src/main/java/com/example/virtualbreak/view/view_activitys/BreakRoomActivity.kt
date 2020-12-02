package com.example.virtualbreak.view.view_activitys

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class BreakRoomActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break_room)

        // Enables Always-on
        setAmbientEnabled()
    }
}