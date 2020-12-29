package com.example.virtualbreak.model

data class Room(var uid: String = "",
                var groupId: String = "",
                var users: HashMap<String, String> = HashMap(),
                var type: Roomtype = Roomtype.COFFEE) {
}