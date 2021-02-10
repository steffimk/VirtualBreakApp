package com.example.virtualbreak.controller.communication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.virtualbreak.R
import com.example.virtualbreak.model.PushNotification
import com.example.virtualbreak.view.view_activitys.NavigationDrawerActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random


private const val CHANNEL_ID = "vb-channel"

/**
 * FirebaseCloudMessagingService
 */
class FCMService : FirebaseMessagingService() {

    private val TAG = "FCMService"

    /**
     * When notification is received: create notification channel, build notification, notify NotificationManager
     * @param msg The received message
     */
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

        val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.dilin_ringtone)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(msg.data["title"])
            .setContentText(msg.data["message"])
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(soundUri)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.drawable.ic_cup_white).color = getColor(R.color.mandarin)
        } else {
            notification.setSmallIcon(R.drawable.ic_vb_alt)
        }

        notificationManager.notify(notificationId, notification.build())
    }

    /**
     * If the FCMToken of a user gets replaced by a new one: Save new one in database
     * @param newToken The new FCmToken
     */
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        PushData.setFcmToken(newToken)
        Log.d(TAG, "NewFCMToken: $newToken")
    }

    /**
     * Creates Notification Channel with specific settings
     * @param notificationManager The notification manager
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.dilin_ringtone)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        val channel = NotificationChannel(CHANNEL_ID, "vb channel name", IMPORTANCE_HIGH).apply {
            description = "Description of channel"
            enableLights(true)
            setSound(soundUri,audioAttributes)
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {

        private val TAG: String = "FCMService Companion"

        /**
         * Builds a new POST-request containing the notification to 'https://fcm.googleapis.com'.
         * @param notification The Notification one wants to send
         */
        fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.postNotificationApi.postNotification((notification))
                if (response.isSuccessful) {
                    Log.d(TAG, "Successfully sent notification")
                } else {
                    Log.e(TAG, response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

        /**
         * Saves the Firebase Cloud Messaging Token in the database.
         */
        fun addFCMTokenListener() {

            Log.d(TAG, "addFCMTokenListener function called")

            Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                if (token != null) {
                    PushData.setFcmToken(token)
                    Log.d(TAG, "Saved fcm token $token")
                }
            })

        }
    }

}