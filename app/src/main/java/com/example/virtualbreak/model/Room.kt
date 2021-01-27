package com.example.virtualbreak.model

data class Room(var uid: String = "",
                var groupId: String = "",
                var description: String = "",
                var users: HashMap<String, String> = HashMap(),
                var messages: HashMap<String, Message> = HashMap(),
                var type: Roomtype = Roomtype.COFFEE,
                var timerEnd: Long? = null,
                var exercise: String? = null,
                var callMembers : HashMap<String, String>? = null,
                // TODO: when changing variable here, also change in constants "DATABASE_CHILD_ROOM_GAME"
                var gameId: String? = null) {
}