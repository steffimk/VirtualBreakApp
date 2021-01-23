package com.example.virtualbreak.model

import com.example.virtualbreak.R

enum class Roomtype (val dbStr : String, val symbol : Int) {
    // TODO: adapt size of symbols
    COFFEE("Kaffee", R.drawable.ic_cup_white),
    QUESTION("Frage", R.drawable.ic_question_white),
    GAME("Spiel", R.drawable.ic_game_white),
    SPORT("Sport", R.drawable.ic_sport)
}