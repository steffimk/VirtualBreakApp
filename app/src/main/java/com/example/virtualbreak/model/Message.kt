package com.example.virtualbreak.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Message (var sender: String = "",
                    var message: String ="",
                    @ServerTimestamp var timestamp: Timestamp ){
}