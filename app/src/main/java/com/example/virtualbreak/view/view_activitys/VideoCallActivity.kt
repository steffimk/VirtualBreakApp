package com.example.virtualbreak.view.view_activitys

import android.content.Intent
import android.os.Bundle
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.view.view_activitys.breakroom.BreakroomWidgetService
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL

/**
 * VideoCallActivity reachable when click on Videocall icon in BreakroomActivity
 */
class VideoCallActivity : JitsiMeetActivity() {
    // Handbook/Documentation: https://jitsi.github.io/handbook/docs/dev-guide/dev-guide-android-sdk

    // build.gradle (root): add maven repository - https://github.com/jitsi/jitsi-maven-repository/raw/master/releases
    // build.gradle (module): add dependency - org.jitsi.react:jitsi-meet-sdk

    // extend Activity from JitsiMeetActivity
    // -> "will be automatically terminated (finish() will be called on the activity) when the conference ends or fails"

    private val JITSI_URL = "https://meet.jit.si"
    var roomId: String? = null
    var userName: String? = null
    var roomName: String? = null
    var roomType: String? = null
    var gameID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var currentName = "user"

        // room & user info
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            roomId = bundle.getString(Constants.ROOM_ID)!!
            if (SharedPrefManager.instance.getWidgetVideoCallManager()) {
                roomName = bundle.getString(Constants.ROOM_NAME)
                roomType = bundle.getString(Constants.ROOM_TYPE)
                gameID = bundle.getString(Constants.GAME_ID)
            }
            userName = bundle.getString(Constants.USER_NAME)
            if (userName != null) {
                currentName = userName as String
            }
        }
        val roomName: String = getString(R.string.app_name) + roomId

        // add user in call list
        PushData.addCallMember(this, roomId)

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
        // Remove member when leaving the call
        PushData.removeCallMember(this, roomId)

        // --- widget ---
        // Allow the widget to open again after leaving the call
        SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(true)
        // Check if Videocall was started from Widget and if yes open it again
        if (SharedPrefManager.instance.getWidgetVideoCallManager()) {
            SharedPrefManager.instance.saveWidgetVideoCallManager(false)
            SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(false)
            val intent = Intent(this, BreakroomWidgetService::class.java)
            intent.putExtra(Constants.ROOM_NAME, roomName)
            intent.putExtra(Constants.ROOM_TYPE, roomType)
            intent.putExtra(Constants.USER_NAME, userName)
            intent.putExtra(Constants.GAME_ID, gameID)
            startService(intent)
        }
    }
}