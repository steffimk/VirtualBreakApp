package com.example.virtualbreak.view.view_activitys

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class SigninActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        // Enables Always-on
        setAmbientEnabled()
    }
}