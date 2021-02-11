package com.example.virtualbreak.model

/**
 * An object representing a hangman game.
 * Gets saved in the database with route: rooms - {roomId} - game - {game}
 */
data class Game (
    var uid: String = "",
    var roomId: String = "",
    var word: String? = null,
    var errors: Int = 0,
    var letters: HashMap<String, String>? = null
){
}