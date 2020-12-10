package com.example.virtualbreak.model

data class Group(var users: HashMap<String, String>,
                 var description: String,
                 var rooms: HashMap<String, String>? = null){
}