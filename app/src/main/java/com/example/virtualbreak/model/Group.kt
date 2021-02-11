package com.example.virtualbreak.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * An object representing a group.
 * Gets saved in the database with route: groups - {groupId} - {group}
 */
@Parcelize
data class Group(var uid: String = "",
                 var users: HashMap<String, String> = HashMap(),
                 var description: String = "",
                 var rooms: HashMap<String, String>? = null
): Parcelable