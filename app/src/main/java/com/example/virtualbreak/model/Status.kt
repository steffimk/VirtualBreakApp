package com.example.virtualbreak.model

enum class Status (val dbStr : String) {
    STUDYING("Beim Arbeiten"), BUSY("Beschäftigt"), AVAILABLE("Verfügbar"),
    ABSENT("Abwesend") , INBREAK("Macht Pause")
}