package com.example.virtualbreak.controller

import android.app.Activity
import android.content.Context

import android.content.SharedPreferences
import android.text.BoringLayout
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.virtualbreak.R
import com.example.virtualbreak.model.Status
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

    val ROOM_ID: String = "roomId"
    val USER_ID: String = "userId"
    val USER_NAME: String = "userName"
    val ROOM_USER: String = "roomUser"
    val CURRENT_STATUS: String = "currentStatus"
    val IS_ALLOWED_TO_OPEN_WIDGET: String = "isWidgetAllowedOpen"
    val WAS_OPEND_FROM_WIDGET: String = "wasOpendFromWidget"


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

    fun saveCurrentStatus(status: Status) {
        sharedPrefs?.let{
            it.edit().putString(CURRENT_STATUS, status.dbStr).apply()
            Log.d(TAG, "Status " + status.dbStr + " added to shared preferences")
        }
        if(sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    /**
     * gets last status that was set in MyProfileFragment, used to revert to old status automatically after leave break room
     */
    fun getSavedStatus(): Status{
        var status: Status? = null
        when(sharedPrefs?.getString(CURRENT_STATUS, null)){
            Status.AVAILABLE.dbStr -> status = Status.AVAILABLE
            Status.BUSY.dbStr -> status = Status.BUSY
            Status.STUDYING.dbStr -> status = Status.STUDYING
            Status.INBREAK.dbStr -> status = Status.INBREAK
            Status.ABSENT.dbStr -> status = Status.ABSENT
            else -> status = Status.ABSENT
        }
        return status
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

    fun saveUserName(userName: String) {

        sharedPrefs?.let{
            it.edit().putString(USER_NAME, userName).apply()
            Log.d(TAG, "UserName " + userName + " added to shared preferences")
        }
        if(sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    fun getUserName(): String?{
        return sharedPrefs?.getString(USER_NAME, null)
    }

    fun removeUserName(){
        sharedPrefs?.edit()?.remove(USER_NAME)?.apply()
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
        if (sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    fun saveIsWidgetAllowedtoOpen(isWidgetallowedtoOpen: Boolean) {
        sharedPrefs?.let {
            it.edit().putBoolean(IS_ALLOWED_TO_OPEN_WIDGET, isWidgetallowedtoOpen).apply()
            Log.d(TAG, "IswidgetOpen " + isWidgetallowedtoOpen + " added to shared preferences")
        }
        if (sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    fun getIsWidgetAllowedtoOpen(): Boolean {
        val isOpen = sharedPrefs?.getBoolean(IS_ALLOWED_TO_OPEN_WIDGET, false)
        if (isOpen != null) {
            return isOpen
        } else {
            return false
        }
    }

    fun saveWidgetVideoCallManager(wasOpendFromWidger: Boolean) {
        sharedPrefs?.let {
            it.edit().putBoolean(WAS_OPEND_FROM_WIDGET, wasOpendFromWidger).apply()
            Log.d(TAG, "IswidgetOpen " + wasOpendFromWidger + " added to shared preferences")
        }
        if (sharedPrefs == null)
            Log.w(TAG, "SharedPrefs is null")
    }

    fun getWidgetVideoCallManager(): Boolean {
        val wasOpendFromWidger = sharedPrefs?.getBoolean(WAS_OPEND_FROM_WIDGET, false)
        if (wasOpendFromWidger != null) {
            return wasOpendFromWidger
        } else {
            return false
        }
    }

    companion object {
        private const val TAG = "SharedPrefManager"

        /**
         * Returns instance
         */
        val instance = SharedPrefManager()
    }
}