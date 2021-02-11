package com.example.virtualbreak.model

import com.example.virtualbreak.controller.Constants

/**
 * An object representing a message.
 * Gets saved in the database with route: rooms - {roomId} - messages - {messageId} - {message}
 */
data class Message(
    var sender: String = "",
    var message: String = "",
    var timestamp: Long = Constants.DEFAULT_TIME
) {
}