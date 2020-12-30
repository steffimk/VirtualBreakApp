package com.example.virtualbreak.controller

import android.app.Activity
import android.content.Context

import android.content.SharedPreferences
import android.util.Log


class SharedPrefManager


/*SETUP AND GENERAL METHODS */
/**
 * Private Constructor to ensure that no object can be initialized externally
 */
private constructor() {
    private var sharedPrefs: SharedPreferences? = null

    val GROUP_ID: String = "groupId"
    val ROOM_ID: String = "roomId"


    /**
     * Initializes the instance.
     */
    fun init(context: Context) {
        Log.d(TAG, "initSharedPrefs()")
        if (sharedPrefs == null) {
            sharedPrefs =
                context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE)
        }
    }

    fun saveGroupId(groupId: String?) {
        sharedPrefs?.let{
            it.edit().putString(GROUP_ID, groupId).apply()
            Log.d(TAG, "GroupId $groupId added to shared preferences")
        }
        if(sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    fun getGroupId(): String? {
        var groupId = sharedPrefs?.getString(GROUP_ID, "")
        Log.d(TAG, "Got groupId from shared preferences: $groupId")
        return groupId
    }

    fun saveRoomId(roomId: String) {

        sharedPrefs?.let{
            it.edit().putString(ROOM_ID, roomId).apply()
            Log.d(TAG, "RoomId " + roomId + " added to shared preferences")
        }
        if(sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    fun getRoomId(): String?{
        return sharedPrefs?.getString(ROOM_ID, null)
    }

    fun removeRoomId(){
        sharedPrefs?.let{
            it.edit().remove("com.example.virtualbreak.roomId").apply()
        }
        if(sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    companion object {
        private const val TAG = "SharedPrefManager"

        /**
         * Returns instance
         */
        val instance = SharedPrefManager()
    }
}