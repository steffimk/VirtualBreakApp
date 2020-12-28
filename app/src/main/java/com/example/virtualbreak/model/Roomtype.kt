package com.example.virtualbreak.model

import com.example.virtualbreak.R

enum class Roomtype (val dbStr : String, val symbol : Int) {
    // TODO: use better symbols
    COFFEE("Coffee", R.drawable.home),
    QUESTION("Question", R.drawable.addfriend),
    GAME ("Games", R.drawable.exit)
}