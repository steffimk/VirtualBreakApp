package com.example.virtualbreak.model

import com.example.virtualbreak.R

/**
 * Enum of all room types. Each room is of one of these types.
 */
enum class Roomtype (val dbStr : String, val symbol : Int) {
    COFFEE("Kaffee", R.drawable.ic_coffee2),
    QUESTION("Frage", R.drawable.ic_question2),
    GAME("Spiel", R.drawable.ic_game2),
    SPORT("Sport", R.drawable.ic_sport2)
}