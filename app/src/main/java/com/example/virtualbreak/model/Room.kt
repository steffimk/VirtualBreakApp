package com.example.virtualbreak.model

data class Room(var users: HashMap<String, String>,
                var type: String) {
}