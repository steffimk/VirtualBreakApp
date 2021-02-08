package com.example.virtualbreak.controller

class Constants {
    companion object {

        const val ROOM_ID = "roomId"
        const val USER_NAME = "userName"
        const val DISPLAY_NAME = "displayName"
        const val ROOM_NAME = "roomName"
        const val ROOM_TYPE = "roomType"
        const val GAME_ID = "gameId"

        const val DATABASE_CHILD_USERS = "users"
        const val DATABASE_CHILD_GROUPS = "groups"
        const val DATABASE_CHILD_ROOMS = "rooms"
        const val DATABASE_CHILD_FRIENDS = "friends"
        const val DATABASE_CHILD_DESCRIPTION = "description"
        const val DATABASE_CHILD_MESSAGES = "messages"
        const val DATABASE_CHILD_STATUS = "status"
        const val DATABASE_CHILD_FCM_TOKEN: String = "fcmToken"
        const val DATABASE_CHILD_FRIEND_REQUESTS = "friendRequests"
        const val DATABASE_CHILD_USERNAME = "username"
        const val DATABASE_CHILD_TIMER_END = "timerEnd"
        const val DATABASE_CHILD_EXERCISE = "exercise"
        const val DATABASE_CHILD_GAMES = "games"
        const val DATABASE_CHILD_ROOM_GAME = "gameId"
        const val DATABASE_CHILD_GAME_LETTERS = "letters"
        const val DATABASE_CHILD_GAME_ERRORS = "errors"
        const val DATABASE_CHILD_CALL_MEMBERS = "callMembers"
        const val DATABASE_CHILD_QUESTION = "question"

        const val DEFAULT_TIME:Long = 0

        const val DEFAULT_MESSAGE_SENDER = "defaultSender"

        const val BASE_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAAq1RP918:APA91bG34UvpK6BFYE1DGNlaHxOFVnuOFTj37u-mob6AbJsTPyVILdksD9XUzt6TSwmY-WyWLEuBe-NCjwIweJWqdqlfriUlOFfNA-dZiz8PLgun4Rj3AUnxwJuB4D7t_na-FVSxhOIl"
        const val CONTENT_TYPE = "application/json"

        const val REQUEST_KEY_GAME_FRAGMENT = "requestKey"
        const val BUNDLE_KEY_GAME_FRAGMENT = "bundleKey"

        const val REQUEST_KEY_GAME_FRAGMENT_CLICK = "requestKeyClick"
        const val BUNDLE_KEY_GAME_FRAGMENT_CLICK = "bundleKeyClick"
        const val CLICK = "click"



        val FITNESS_IDEAS = arrayOf(
            Pair("Liegestützen",1.3), Pair("Hampelmänner",2.0), Pair("Sit-Ups",1.0), Pair("Plank",2.0), Pair("Kniebeugen",1.0),
            Pair("Mountain Climber",1.0), Pair("Superman",0.3), Pair("Dehnen",2.0), Pair("Burpees",2.0), Pair("Side Plank",1.0),
            Pair("Ausfallschritt",1.0), Pair("Crunches",1.3), Pair("Donkey Kicks",1.0), Pair("Seilspringen",3.0))

        val HANGMAN_WORDS = listOf<String>(
            "Chrysantheme",
            "Kernspintomografie",
            "Puzzle",
            "Schifffahrtsgesellschaft",
            "Puderzucker",
            "Zwiebelsuppe",
            "Quiz",
            "Espressomaschine",
            "Bugfix",
            "Prokrastination",
            "Geburtstagstorte",
            "Ausgangsbeschraenkung",
            "Homeoffice",
            "Terrasse",
            "Zucchini",
            "Portemonnaie",
            "Orchideenzucht",
            "Gurkensalat",
            "Karottenkuchen",
            "Kaninchen",
            "Giraffenhals",
            "Elefantenhaus",
            "Ohrenschmerzen",
            "Unwetterwarnung",
            "Desinfektionsmittelspender",
            "Blubberblaeschen",
            "Kaffeeklatsch",
            "Kaulquappe",
            "Verschlimmbesserung",
            "Halbmond",
            "Achteck",
            "Surfbrett",
            "Rettungsschwimmer",
            "Gabelstapler",
            "Schlauchboot",
            "Heissluftballonfahrt",
            "Fallschirmspringen",
            "Halsnasenohrenarztpraxis",
            "Regenbogenfarben",
            "Rechtschreibfehler",
            "Tintenfisch",
            "Hummer",
            "Seeigel",
            "Manati",
            "Zufallsgenerator",
            "Rentierkutsche",
            "Schokoladenfabrikmitarbeiter",
            "Kirschkernweitspuckwettbewerb",
            "Arbeitsunfaehigkeitsbescheinigung",
            "Renaturierung"
        )

        val HANGMAN_MAX_ERRORS = 7
    }
}