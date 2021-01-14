package com.example.virtualbreak.controller.communication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.virtualbreak.R
import com.example.virtualbreak.model.PushNotification
import com.example.virtualbreak.view.view_activitys.NavigationDrawerActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val CHANNEL_ID = "vb-channel"

// FirebaseCloudMessagingService
class FCMService : FirebaseMessagingService() {

    private val TAG = "FCMService"

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)
        Log.d(TAG, "Received message with data " + msg.data + "" + msg.notification)
        val intent = Intent(this, NavigationDrawerActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d(TAG, "notification manager: " + notificationManager)
        val notificationId = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(msg.data["title"])
            .setContentText(msg.data["message"])
            .setSmallIcon(R.drawable.ic_vb_alt_playstore)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * If the FCMToken of a user gets replaced by a new one: Save new one in database
     */
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        PushData.setFcmToken(newToken)
        Log.d(TAG, "NewFCMToken: $newToken")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(CHANNEL_ID, "vb channel name", IMPORTANCE_HIGH).apply {
            description = "Description of channel"
            enableLights(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {

        private val TAG: String = "FCMService Companion"

        fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification((notification))
                if (response.isSuccessful) {
                    Log.d(TAG, "Successfully sent notification")
                } else {
                    Log.e(TAG, response.errorBody().toString())
                }
            } catch (ex: Exception) {
                Log.e(TAG, ex.toString())
            }
        }

        /**
         * Subscribe to a topic to receive all notifications send out for this topic
         */
        fun subscribeToTopic(topic: String) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d(TAG, "Could not subscribe to topic $topic")
                } else {
                    Log.d(TAG, "Successfully subscribed to topic $topic")
                }
            }
        }
    }

}