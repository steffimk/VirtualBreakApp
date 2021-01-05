package com.example.virtualbreak.model

import com.example.virtualbreak.controller.Constants

data class Message(
    var sender: String = "",
    var message: String = "",
    var timestamp: Long = Constants.DEFAULT_TIME
) {
}