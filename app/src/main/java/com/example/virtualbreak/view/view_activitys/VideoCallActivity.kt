package com.example.virtualbreak.view.view_activitys

import android.os.Bundle
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.communication.PushData
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL


class VideoCallActivity : JitsiMeetActivity() {

    private val JITSI_URL = "https://meet.jit.si"
    var roomId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var currentName = "user"

        // room & user info
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            roomId = bundle.getString(Constants.ROOM_ID)!!
            val userName = bundle.getString(Constants.USER_NAME)
            if (userName != null) {
                currentName = userName
            }
        }
        val roomName: String = getString(R.string.app_name) + roomId

        // add user in call list
        PushData.addCallMember(roomId)

        // user info
        var userInfoBundle = Bundle()
        userInfoBundle.putString(Constants.DISPLAY_NAME, currentName)
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

    override fun onDestroy() {
        super.onDestroy()
        PushData.removeCallMember(roomId)
    }
}