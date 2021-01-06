package com.example.virtualbreak.model

import com.example.virtualbreak.R

enum class Roomtype (val dbStr : String, val symbol : Int) {
    // TODO: use better symbols
    COFFEE("Coffee", R.drawable.ic_cup_white),
    QUESTION("Question", R.drawable.ic_question_white),
    GAME("Games", R.drawable.ic_game_white),
    SPORT("Sport", R.drawable.ic_sport)
}