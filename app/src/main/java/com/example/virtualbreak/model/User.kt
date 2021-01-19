package com.example.virtualbreak.model

data class User(var uid: String = "",
                var username: String = "",
                var email: String = "",
                var status: Status? = null,
                var isSelected: Boolean = false,
                var groups: HashMap<String, String>? = null,
                var friends: HashMap<String, String>? = null,
                // HashMap<userId,isIncoming> -> Value is true if friendRequest is incoming and false if it is outgoing
                var friendRequests: HashMap<String,Boolean>? = null,
                //FirebaseCloudMessagingToken
                var fcmToken: String = ""
)
