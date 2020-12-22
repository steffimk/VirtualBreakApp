package com.example.virtualbreak.model

data class User(var uid: String = "",
                var username: String = "",
                var email: String = "",
                var status: Status? = null,
                var profilePicture: String? = null,
                var groups: HashMap<String, String>? = null,
                var friends: HashMap<String, String>? = null) {
    // val username -> can only be set one time (when initializing) - only getter
    // the others (var) can be set later  - getter and setter
}
