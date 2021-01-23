package com.example.virtualbreak.controller

class Constants {
    companion object {
        const val ROOM_ID = "roomId"
        const val USER_NAME = "userName"
        const val DISPLAY_NAME = "displayName"
        const val ROOM_NAME = "roomName"
        const val ROOM_TYPE = "roomType"

        const val DATABASE_CHILD_USERS = "users"
        const val DATABASE_CHILD_GROUPS = "groups"
        const val DATABASE_CHILD_ROOMS = "rooms"
        const val DATABASE_CHILD_FRIENDS = "friends"
        const val DATABASE_CHILD_DESCRIPTION = "description"
        const val DATABASE_CHILD_MESSAGES = "messages"
        const val DATABASE_CHILD_STATUS = "status"
        const val DATABASE_CHILD_PROFILE_PICTURE = "profilePicture"
        const val DATABASE_CHILD_FRIEND_REQUESTS = "friendRequests"

        const val DEFAULT_TIME:Long = 0
        const val DEFAULT_MESSAGE_SENDER = "defaultSender"
    }
}