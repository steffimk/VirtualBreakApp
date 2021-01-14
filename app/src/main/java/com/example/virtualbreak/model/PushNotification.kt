package com.example.virtualbreak.model

data class PushNotification(
    val data: NotificationData,
    val notification: NotificationBody,
    val to: String
)