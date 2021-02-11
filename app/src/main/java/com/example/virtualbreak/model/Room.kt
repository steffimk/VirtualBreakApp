package com.example.virtualbreak.model

/**
 * An object representing a room.
 * Gets saved in the database with route: rooms - {roomId} - {room}
 */
data class Room(var uid: String = "",
                var groupId: String = "",
                var description: String = "",
                var users: HashMap<String, String> = HashMap(),
                var messages: HashMap<String, Message> = HashMap(),
                var type: Roomtype = Roomtype.COFFEE,
                var timerEnd: Long? = null,
                var exercise: String? = null,
                var question: String? = null,
                var callMembers : HashMap<String, String>? = null,
                var gameId: String? = null) {
}