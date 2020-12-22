package com.example.virtualbreak.model

data class Group(var uid: String = "",
                 var users: HashMap<String, String> = HashMap(),
                 var description: String = "",
                 var rooms: HashMap<String, String>? = null){
}