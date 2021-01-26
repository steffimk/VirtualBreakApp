package com.example.virtualbreak.model

import com.example.virtualbreak.controller.Constants

data class Game (
    var uid: String = "",
    var roomId: String = "",
    var word: String? = null,
    // TODO: when chanigng value here also change "DATABASE_CHILD_GAME_LETTERS", "DATABASE_CHILD_GAME_ERRORS" in constants
    var errors: Int = 0,
    var letters: HashMap<String, String>? = null
){
}