package com.example.virtualbreak.controller.communication.boredapi

import com.example.virtualbreak.model.BoredActivity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BoredAPI {
    @GET("/top.json")
    fun getTop(@Query("after") after: String,
               @Query("limit") limit: String)
            : Call<BoredActivity>;
}