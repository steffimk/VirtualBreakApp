package com.example.virtualbreak.view.view_fragments.logout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.view.view_activitys.MainActivity
import com.example.virtualbreak.view.view_activitys.breakroom.BreakroomWidgetService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class LogoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_logout, container, false)

        root.findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            PushData.setStatus(Status.ABSENT)
            //close Widget
            activity?.stopService(Intent(activity, BreakroomWidgetService::class.java))
            //Sign out
            Firebase.auth.signOut()
            //Unregister from fcm push notifications
            Firebase.messaging.isAutoInitEnabled = false
            Firebase.messaging.deleteToken()
            FirebaseInstallations.getInstance().delete()
            startActivity(
                Intent(
                    activity,
                    MainActivity::class.java
                )
            ) //go back to sign up/login activity
        }

        return root
    }
}