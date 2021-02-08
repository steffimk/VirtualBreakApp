package com.example.virtualbreak.controller.communication

import com.example.virtualbreak.controller.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A REST client used for sending POST-requests to https://fcm.googleapis.com to send push notifications
 */
class RetrofitInstance {

    companion object {
        /**
         * The retrofit instance that controls requests and responses t https://fcm.googleapis.com
         * Serialization to and from JSON with GSON
         */
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        /**
         * Builds an implementation with NotificationAPI as endpoint
         */
        val postNotificationApi: NotificationAPI by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}