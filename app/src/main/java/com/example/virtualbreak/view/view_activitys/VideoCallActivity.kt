package com.example.virtualbreak.view.view_activitys

import android.os.Bundle
import com.example.virtualbreak.R
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL


class VideoCallActivity : JitsiMeetActivity() {

    private val JITSI_URL = "https://meet.jit.si"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // room info
        var roomId = ""
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            roomId = bundle.getString("room_id")!!
        }
        val roomName: String = getString(R.string.app_name) + roomId

        // user info
        // TODO: get own username
        val currentName = "user name"

        var userInfoBundle = Bundle()
        userInfoBundle.putCharSequence("displayName", currentName)
        val userInfo = JitsiMeetUserInfo(userInfoBundle)

        // how to start jitsi conference
        val options = JitsiMeetConferenceOptions.Builder()
            .setServerURL(URL(JITSI_URL))
            .setRoom(roomName)
            .setAudioMuted(false)
            .setVideoMuted(false)
            .setAudioOnly(false)
            .setWelcomePageEnabled(false)
            .setUserInfo(userInfo)
            .build()

        join(options)
    }
}