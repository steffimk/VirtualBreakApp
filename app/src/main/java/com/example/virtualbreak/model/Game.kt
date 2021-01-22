package com.example.virtualbreak.model

import com.example.virtualbreak.controller.Constants

data class Game (
    var word: String = "",
    var errors: Int = 0,
    var letters: HashMap<String, String> = HashMap()
){
}