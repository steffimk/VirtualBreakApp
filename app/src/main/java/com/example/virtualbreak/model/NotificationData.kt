package com.example.virtualbreak.model

/**
 * An object representing the data of a push notification.
 */
data class NotificationData (
    val title: String,
    val message: String
)

/**
 * An object representing the body of a push notification.
 */
data class NotificationBody (
    val title: String,
    val body: String
)