package com.example.virtualbreak.model


data class Game (
    var uid: String = "",
    var roomId: String = "",
    var word: String? = null,
    var errors: Int = 0,
    var letters: HashMap<String, String>? = null
){
}