package com.example.virtualbreak.controller

import android.app.Activity
import android.content.Context

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class SharedPrefManager


/*SETUP AND GENERAL METHODS */
/**
 * Private Constructor to ensure that no object can be initialized externally
 */
private constructor() {
    private var sharedPrefs: SharedPreferences? = null

    val GROUP_ID: String = "groupId"
    val ROOM_ID: String = "roomId"
    val USER_ID: String = "userId"
    val ROOM_USER: String = "roomUser"


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
        sharedPrefs?.edit()?.remove(ROOM_ID)?.apply()
        if(sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    fun saveUserId(userId: String) {

        sharedPrefs?.let{
            it.edit().putString(USER_ID, userId).apply()
            Log.d(TAG, "UserId " + userId + " added to shared preferences")
        }
        if(sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    fun getUserId(): String?{
        return sharedPrefs?.getString(USER_ID, null)
    }

    fun removeUserId(){
        sharedPrefs?.edit()?.remove(USER_ID)?.apply()
        if(sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    /**
     * for saving hashmap with user ids and usernames
     */
    fun getRoomUsersHashmap(): HashMap<String, String>? {
        Log.d(TAG, "getRoomUsersHashmap")

        val gson = Gson()
        //get hashmap from shared prefs
        val storedHashMapString = sharedPrefs?.getString(ROOM_USER, null)
        if(sharedPrefs== null)
            Log.w(TAG, "SharedPrefs null")
        if(storedHashMapString== null)
            Log.w(TAG, "storedHashMapString null")

        storedHashMapString?.let{
            val type: Type =
                object : TypeToken<HashMap<String?, String?>?>() {}.getType()
            val roomUsers: HashMap<String, String>? =
                gson.fromJson(storedHashMapString, type)
            return roomUsers
        }

        Log.w(TAG, "getRoomUsersHashmap() error")
        return null
    }

    fun removeRoomUsers() {
        Log.d(TAG, "removeRoomUsers()")
        sharedPrefs?.edit()?.remove(ROOM_USER)?.apply()
        if(sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    fun saveRoomUsers(hashMapString: String) {
        Log.d(TAG, "saveRoomUsers()")
        sharedPrefs?.edit()?.putString(ROOM_USER, hashMapString)?.apply()
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