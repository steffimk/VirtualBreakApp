package com.example.virtualbreak.model

data class Room(var users: HashMap<String, String> = HashMap(),
                var type: Roomtype = Roomtype.COFFEE) {
}