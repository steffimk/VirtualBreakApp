package com.example.virtualbreak.view.view_fragments.logout

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.ktx.auth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Fragment with logout button
 */
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
            //Unregister from fcm push notifications
            Firebase.messaging.isAutoInitEnabled = false
            Firebase.messaging.deleteToken().addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Log.d("LogoutFragment", "Token successfully deleted")
                    //Sign out
                    Firebase.auth.signOut()
                    Log.d("LogoutFragment", "Signed out")
                    GlobalScope.launch {
                        FirebaseInstallations.getInstance().delete()
                    }
                    startActivity(Intent(activity, MainActivity::class.java)) // go back to sign up/login activity
                } else {
                    Log.d("LogoutFragment", "Problem deleting token")
                    // do nothing
                }
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addTransition()
    }

    private fun addTransition() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
    }
}