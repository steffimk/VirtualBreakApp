package com.example.virtualbreak.model

/**
 * Use this class to initiate a FCM-PushNotification.
 * Gets sent to the FirebaseCloudMessaging-API
 */
data class PushNotification(
    val data: NotificationData,
    val notification: NotificationBody,
    val to: String
)