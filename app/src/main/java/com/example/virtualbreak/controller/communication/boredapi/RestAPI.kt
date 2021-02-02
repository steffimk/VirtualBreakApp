package com.example.virtualbreak.controller.communication.boredapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestAPI {
    private val boredApi: BoredAPI

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.boredapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        boredApi = retrofit.create(BoredAPI::class.java)
    }
}