package com.example.virtualbreak.controller

class Constants {
    companion object {

        const val ROOM_ID = "roomId"
        const val USER_NAME = "userName"
        const val DISPLAY_NAME = "displayName"
        const val ROOM_TYPE = "roomType"
        const val GAME_ID = "gameId"

        const val DATABASE_CHILD_USERS = "users"
        const val DATABASE_CHILD_GROUPS = "groups"
        const val DATABASE_CHILD_ROOMS = "rooms"
        const val DATABASE_CHILD_FRIENDS = "friends"
        const val DATABASE_CHILD_DESCRIPTION = "description"
        const val DATABASE_CHILD_MESSAGES = "messages"
        const val DATABASE_CHILD_STATUS = "status"
        const val DATABASE_CHILD_FCM_TOKEN: String = "fcmToken"
        const val DATABASE_CHILD_FRIEND_REQUESTS = "friendRequests"
        const val DATABASE_CHILD_USERNAME = "username"
        const val DATABASE_CHILD_GAMES = "games"
        const val DATABASE_CHILD_ROOM_GAME = "gameId"
        const val DATABASE_CHILD_GAME_LETTERS = "letters"
        const val DATABASE_CHILD_GAME_ERRORS = "errors"

        const val DEFAULT_TIME:Long = 0

        const val DEFAULT_MESSAGE_SENDER = "defaultSender"

        const val BASE_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAAq1RP918:APA91bG34UvpK6BFYE1DGNlaHxOFVnuOFTj37u-mob6AbJsTPyVILdksD9XUzt6TSwmY-WyWLEuBe-NCjwIweJWqdqlfriUlOFfNA-dZiz8PLgun4Rj3AUnxwJuB4D7t_na-FVSxhOIl"
        const val CONTENT_TYPE = "application/json"
    }
}