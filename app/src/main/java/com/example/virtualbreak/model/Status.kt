package com.example.virtualbreak.model

/**
 * Enum of all different statuses. Each user is of one of these statuses.
 */
enum class Status (val dbStr : String) {
    STUDYING("Beim Arbeiten"), BUSY("Beschäftigt"), AVAILABLE("Verfügbar"),
    ABSENT("Abwesend") , INBREAK("Macht Pause")
}