package com.example.virtualbreak.controller.communication

import com.example.virtualbreak.controller.Constants.Companion.CONTENT_TYPE
import com.example.virtualbreak.controller.Constants.Companion.SERVER_KEY
import com.example.virtualbreak.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    /**
     * Interface of the API to send the post request of a notification to https://fcm.googleapis.com
     * @param notification The notification one wants to send
     */
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(@Body notification: PushNotification): Response<ResponseBody>

}