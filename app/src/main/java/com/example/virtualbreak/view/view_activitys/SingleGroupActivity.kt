package com.example.virtualbreak.view.view_activitys

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class SingleGroupActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_group)

        // Enables Always-on
        setAmbientEnabled()
    }
}