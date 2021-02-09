package com.example.virtualbreak.controller.communication

import android.content.Context
import android.util.Log
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.model.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.HashMap

class PushData {

    companion object {

        private val database : DatabaseReference = Firebase.database.reference
        private const val TAG: String = "PushData"

        //------------------------  Methods concerning user ------------------------

        /**
         * Creates user on firebase
         *
         * @param user
         * @param name Username
         */
        fun saveUser(user: FirebaseUser, name: String) {
            if (user?.email != null) {
                  val userData = User(user.uid, name, user.email!!, Status.AVAILABLE)
                  database.child(Constants.DATABASE_CHILD_USERS).child(user.uid).setValue(userData)
                Log.d(TAG, "Saved user to realtime database")
                Log.d(TAG, "User: $userData")
            } else {
                Log.d(TAG, "No user logged in. Cannot save user.")
            }
        }

        /**
         * Setting the username of a user
         *
         * @param name Username that should be set
         */
        fun saveUserName(name: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_USERNAME)
                    .setValue(name)
                Log.d(TAG, "Saved username")
            } else {
                Log.d(TAG, "No user logged in. Cannot save user.")
            }
        }

        /**
         * Set status of user
         *
         * @param status Status to be set
         */
        fun setStatus(status: Status) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_STATUS).setValue(status)
            } else {
                Log.d(TAG, "No user logged in. Cannot change status.")
            }
        }

        /**
         * Set status of user
         */
        fun resetStatusToBeforeBreak() {
            val currentUserId = Firebase.auth.currentUser?.uid
            val statusBeforeBreak = SharedPrefManager.instance.getSavedStatus()
            Log.d(TAG, "resetStatusToBeforeBreak "+statusBeforeBreak.dbStr)
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_STATUS).setValue(statusBeforeBreak)
            } else {
                Log.d(TAG, "No user logged in. Cannot change status.")
            }
        }

        /**
         * Store Fcm Token to user
         *
         * @param fcmToken Token to be set
         */
        fun setFcmToken(fcmToken: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_FCM_TOKEN).setValue(fcmToken)
            } else {
                Log.d(TAG, "No user logged in. Cannot set fcmToken.")
            }
        }

        //------------------------  Methods concerning group ------------------------

        /**
         * Creates a group
         *
         * @param description Group description
         * @param userIds Users which should be added to grou
         */
        fun saveGroup(description: String, userIds: Array<String>?) : String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val groupId = database.child(Constants.DATABASE_CHILD_GROUPS).push().key
                if (groupId != null) {
                    val newGroup = Group(groupId, hashMapOf(currentUserId to currentUserId), description)
                    database.child(Constants.DATABASE_CHILD_GROUPS).child(groupId).setValue(newGroup)
                    database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_GROUPS).child(groupId).setValue(groupId)
                    userIds?.forEach{
                        joinGroup(groupId, it)
                    }
                    Log.d(TAG, "Saved new group")
                }
                return groupId
            } else {
                Log.d(TAG, "No user logged in. Cannot save group.")
                return null
            }
        }

        /**
         * When user wants to join group
         *
         * @param groupId Id of the group that should be joined
         * @param userId User that joins the group
         */
        private fun joinGroup(groupId: String, userId: String) {
            database.child(Constants.DATABASE_CHILD_GROUPS).child(groupId)
                .child(Constants.DATABASE_CHILD_USERS).child(userId).setValue(userId)
            database.child(Constants.DATABASE_CHILD_USERS).child(userId)
                .child(Constants.DATABASE_CHILD_GROUPS).child(groupId).setValue(groupId)
            Log.d(TAG, "User $userId joined group $groupId")
        }

        /**
         * When leaving a group, remove user from group and group from user
         * Also check if it's the last user to delete groups that are not needed anymore
         *
         * @param group Group object that should be left
         */
        fun leaveGroup(group: Group) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {

                if(group.users.size == 1){
                    //delete group, because only member is current user
                    deleteGroup(group)
                }
                else{
                    //remove group from user's internal group list
                    database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId)
                        .child(Constants.DATABASE_CHILD_GROUPS).child(group.uid).removeValue()

                    //removes user from group's user list
                    database.child(Constants.DATABASE_CHILD_GROUPS).child(group.uid)
                        .child(Constants.DATABASE_CHILD_USERS).child(currentUserId)
                        .removeValue()

                    //remove user from every room's user list in the group
                    group.rooms?.forEach {
                        database.child(Constants.DATABASE_CHILD_ROOMS).child(it.value)
                            .child(Constants.DATABASE_CHILD_USERS).child(currentUserId).removeValue()
                    }
                }

            } else {
                Log.d(TAG, "No user logged in. Cannot leave group.")
            }
        }

        /**
         * Deleting a group and all its corresponding content
         *
         * @param group Group object that should be deleted
         */
        private fun deleteGroup(group: Group) {
            for (user in group.users) { //remove group from internal users group lists
                database.child(Constants.DATABASE_CHILD_USERS).child(user.key).child(Constants.DATABASE_CHILD_GROUPS).child(group.uid).removeValue()
            }
            group.rooms?.forEach { //remove remove all rooms of goup
                database.child(Constants.DATABASE_CHILD_ROOMS).child(it.value).removeValue()
            }
            //remove group itself
            database.child(Constants.DATABASE_CHILD_GROUPS).child(group.uid).removeValue()
        }

        fun setGroupDescription(groupId: String, description: String) {
            database.child(Constants.DATABASE_CHILD_GROUPS).child(groupId).child(Constants.DATABASE_CHILD_DESCRIPTION).setValue(description)
        }

        //------------------------  Methods concerning game ------------------------

        /**
         * Updating the game with a new game
         *
         * @param gameId Id of the current game
         * @param roomId Id of the current room
         */
        fun updateGame(gameId: String, roomId: String): String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val randomValue = (0..(Constants.HANGMAN_WORDS.size - 1)).random()
                val randomWord = Constants.HANGMAN_WORDS.get(randomValue)
                val newGame = Game(gameId, roomId, randomWord)
                database.child(Constants.DATABASE_CHILD_GAMES).child(gameId).setValue(newGame)
                database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId)
                    .child(Constants.DATABASE_CHILD_ROOM_GAME).setValue(gameId)
                Log.d(TAG, "Updated new game")

                return gameId
            } else {
                Log.d(TAG, "No user logged in. Cannot update game.")
                return null
            }
        }

        /**
         * Creating and adding a new game in the current room
         *
         * @param roomId Id of the current room
         */
        fun createGame(roomId: String): String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val gameId = database.child(Constants.DATABASE_CHILD_GAMES).push().key
                if (gameId != null) {
                    val randomValue = (0..(Constants.HANGMAN_WORDS.size-1)).random()
                    Log.i(TAG, "random value: " + randomValue)
                    val randomWord = Constants.HANGMAN_WORDS.get(randomValue)
                    val newGame = Game(gameId, roomId, randomWord)
                    database.child(Constants.DATABASE_CHILD_GAMES).child(gameId).setValue(newGame)
                    database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId)
                        .child(Constants.DATABASE_CHILD_ROOM_GAME).setValue(gameId)
                    Log.d(TAG, "Saved new game")
                }
                return gameId
            } else {
                Log.d(TAG, "No user logged in. Cannot save game.")
                return null
            }
        }

        /**
         * Updating the letter list of the game
         *
         * @param gameId Id of the current game
         * @param letter Letter that should be added to letter list of game
         */
        fun addLetterToGame(gameId: String, letter: String) {
            database.child(Constants.DATABASE_CHILD_GAMES).child(gameId).child(Constants.DATABASE_CHILD_GAME_LETTERS).child(letter).setValue(letter)
            Log.d(TAG, "added letter to game")
        }

        /**
         * Updating the errors of the game
         *
         * @param gameId Id of the current game
         * @param errors Number of errors that should be set
         */
        fun addError(gameId: String, errors: Int) {
            database.child(Constants.DATABASE_CHILD_GAMES).child(gameId).child(Constants.DATABASE_CHILD_GAME_ERRORS).setValue(errors)
            Log.d(TAG, "added error to game")
        }

        //------------------------  Methods concerning call ------------------------

        /**
         * Add user to the call member list of the room
         *
         * @param context
         * @param roomId Id of the current room
         */
        fun addCallMember(context:Context, roomId: String?) {
            val currentUserId = Firebase.auth.currentUser?.uid
            val userName = SharedPrefManager.instance.getUserName()
            if (currentUserId != null && roomId != null) {
                if(userName != null){
                    sendSystemMessage(roomId,userName + " " + context.getString(R.string.joined_call))
                } else{
                    sendSystemMessage(roomId,currentUserId + " " +context.getString(R.string.joined_call))
                }
                database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId)
                    .child(Constants.DATABASE_CHILD_CALL_MEMBERS).child(currentUserId)
                    .setValue(currentUserId)
                Log.d(TAG, "Saved new call member")
            } else {
                Log.d(TAG, "No user logged in. Cannot save call member.")
            }
        }

        /**
         * Remove user from the call member list of the room
         *
         * @param context
         * @param roomId Id of the current room
         */
        fun removeCallMember(context:Context,roomId: String?) {
            val currentUserId = Firebase.auth.currentUser?.uid
            val userName = SharedPrefManager.instance.getUserName()
            if (currentUserId != null && roomId != null) {
                if(userName != null){
                    sendSystemMessage(roomId,userName + " " + context.getString(R.string.left_call))
                } else{
                    sendSystemMessage(roomId,currentUserId + " " +context.getString(R.string.left_call))
                }
                database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId)
                    .child(Constants.DATABASE_CHILD_CALL_MEMBERS).child(currentUserId).removeValue()
                Log.d(TAG, "Removed call member")

            } else {
                Log.d(TAG, "No user logged in. Cannot remove call member.")
            }
        }

        //------------------------  Methods concerning room and chat ------------------------

        /**
         * Saving a new room
         *
         * @param groupId Id of the group where the room belongs to
         * @param roomType Type of the room
         * @param roomDescription Description of room that should be set
         */
        fun saveRoom(groupId: String, roomType: Roomtype, roomDescription: String) : String? {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val roomId = database.child(Constants.DATABASE_CHILD_ROOMS).push().key
                if (roomId != null) {
                    val newRoom = Room(roomId, groupId, roomDescription, hashMapOf(currentUserId to currentUserId), HashMap(), roomType)
                    database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).setValue(newRoom)
                    database.child(Constants.DATABASE_CHILD_GROUPS).child(groupId).child(Constants.DATABASE_CHILD_ROOMS).child(roomId).setValue(roomId)
                    Log.d(TAG, "Saved new room " + newRoom)
                }
                return roomId
            } else {
                Log.d(TAG, "No user logged in. Cannot save room.")
                return null
            }
        }

        /**
         * When user joins room
         *
         * @param context
         * @param roomId Id of the current room
         * @param userName Name of the user that wants to join
         */
        fun joinRoom(context:Context, roomId: String, userName : String?) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId)
                    .child(Constants.DATABASE_CHILD_USERS).child(currentUserId)
                    .setValue(currentUserId)
                if (userName != null) {
                    sendSystemMessage(roomId, userName + " " + context.getString(R.string.joined))
                } else {
                    sendSystemMessage(
                        roomId,
                        currentUserId + " " + context.getString(R.string.joined)
                    )
                }
                Log.d(TAG,"User joined room with id $roomId")
            } else {
                Log.d(TAG, "No user logged in. Cannot join room.")
            }
        }

        /**
         * When leaving a room
         * Also check if it's the last user to delete rooms that are not needed anymore
         *
         * @param context
         * @param room Room that should be left
         * @param userName Name of the user that wants to leave
         */
        fun leaveRoom(context: Context, room: Room?, userName: String?) {
            if (room == null) return

            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                if (room.users.size == 1 && room.users.containsKey(currentUserId)) {
                    database.child(Constants.DATABASE_CHILD_GROUPS).child(room.groupId).child(Constants.DATABASE_CHILD_ROOMS).child(room.uid).removeValue()
                    database.child(Constants.DATABASE_CHILD_ROOMS).child(room.uid).removeValue()
                    if(room.type == Roomtype.GAME){
                        if(room.gameId != null){
                            database.child(Constants.DATABASE_CHILD_GAMES).child(room.gameId!!).removeValue()
                        }
                    }
                    Log.d(TAG, "Deleted empty room.")
                } else {
                    database.child(Constants.DATABASE_CHILD_ROOMS).child(room.uid).child(Constants.DATABASE_CHILD_USERS).child(currentUserId)
                        .removeValue()
                    if(userName != null){
                        sendSystemMessage(room.uid,userName + " " + context.getString(R.string.left))
                    } else{
                        sendSystemMessage(
                            room.uid,
                            currentUserId + " " + context.getString(R.string.left)
                        )
                    }

                    Log.d(TAG, "Removed user from room")
                }
            } else {
                Log.d(TAG, "No user logged in. Cannot leave room.")
            }
        }

        /**
         * Deleting a room and all its corresponding content
         *
         * @param room Room to delete
         */
        fun deleteRoom(room: Room?) {
            if (room == null) {
                return
            }
//            if (room.users.size == 0) {
            else {
                database.child(Constants.DATABASE_CHILD_GROUPS).child(room.groupId)
                    .child(Constants.DATABASE_CHILD_ROOMS).child(room.uid).removeValue()
                database.child(Constants.DATABASE_CHILD_ROOMS).child(room.uid).removeValue()
                if (room.type == Roomtype.GAME) {
                    if (room.gameId != null) {
                        database.child(Constants.DATABASE_CHILD_GAMES).child(room.gameId!!)
                            .removeValue()
                    }
                }
                Log.d(TAG, "Deleted empty room.")
            }
        }

        /**
         * Setting the description of a room
         *
         * @param roomId Id of the current room
         * @param description New description that should be set
         */
        fun setRoomDescription(roomId: String, description: String) {
            database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId)
                .child(Constants.DATABASE_CHILD_DESCRIPTION).setValue(description)
        }

        /**
         * Store sended message
         *
         * @param roomId Id of the current room
         * @param message Message that should be stored to room
         */
        fun sendMessage(roomId: String, message: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val date = Date()
                val newChatMessage = Message(currentUserId, message, date.time)
                database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_MESSAGES).push().setValue(newChatMessage)
            } else {
                Log.d(TAG, "No user logged in. Cannot send message.")
            }
        }

        /**
         * Store messages that comes from the app
         *
         * @param roomId Id of the current room
         * @param message Message that should be stored to room
         */
        private fun sendSystemMessage(roomId: String, message: String){
            val date = Date()
            val newChatMessage = Message(Constants.DEFAULT_MESSAGE_SENDER, message, date.time)
            database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_MESSAGES).push().setValue(newChatMessage)
        }

        //------------------------  Methods concerning sports room  ------------------------

        /**
         * Add sport to room
         *
         * @param roomId Id of the current room
         * @param minutes Minute amount of timer
         * @param seconds Seconds amount of timer
         * @param exercise Sport exercise
         */
        fun startNewTimer(roomId: String, minutes: Int, seconds: Int, exercise: String){
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_EXERCISE).setValue(exercise)
                val currentTime = Date().time
                val timerEnd = currentTime + minutes*60000 + seconds*1000
                database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_TIMER_END).setValue(timerEnd)
            } else {
                Log.d(TAG, "No user logged in. Cannot start new timer.")
            }
        }

        /**
         * Remove timer from room
         *
         * @param roomId Id of the current room
         */
        fun removeTimer(roomId: String){
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_TIMER_END).setValue(null)
            } else {
                Log.d(TAG, "No user logged in. Cannot remove timer.")
            }
        }

        //------------------------  Methods concerning question room  ------------------------

        /**
         * Add question to room
         *
         * @param roomId Id of the current room
         * @param question Question to add
         */
        fun saveQuestion(roomId: String, question: String) {
            database.child(Constants.DATABASE_CHILD_ROOMS).child(roomId).child(Constants.DATABASE_CHILD_QUESTION).setValue(question)
        }

        //------------------------  Methods concerning friends and friend requests  ------------------------

        /**
         * sends friend request: logic: id for user id - boolean: incoming (true) or outgoing (false)
         */
        fun sendFriendRequest(friendId: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_FRIEND_REQUESTS).child(friendId).setValue(false)
                database.child(Constants.DATABASE_CHILD_USERS).child(friendId).child(Constants.DATABASE_CHILD_FRIEND_REQUESTS).child(currentUserId).setValue(true)
            } else {
                Log.d(TAG, "No user logged in. Cannot send friend request.")
            }
        }

        /**
         * Before calling this function, check whether user really got friend request of user with passed on id
         */
        fun confirmFriendRequest(friendId: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                // add users to each others friends
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_FRIENDS).child(friendId).setValue(friendId)
                database.child(Constants.DATABASE_CHILD_USERS).child(friendId).child(Constants.DATABASE_CHILD_FRIENDS).child(currentUserId).setValue(currentUserId)
                // Remove users from each others friend requests
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_FRIEND_REQUESTS).child(friendId).removeValue()
                database.child(Constants.DATABASE_CHILD_USERS).child(friendId).child(Constants.DATABASE_CHILD_FRIEND_REQUESTS).child(currentUserId).removeValue()
            } else {
                Log.d(TAG, "No user logged in. Cannot confirm friend request.")
            }
        }

        /**
         * if you sent a friend request and decide you want to delete the request
         * or if you got a friend request and don't want to accept - delete request go back to normal
         */
        fun removeFriendRequest(friendId: String){
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                // Remove users from each others friend requests
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_FRIEND_REQUESTS).child(friendId).removeValue()
                database.child(Constants.DATABASE_CHILD_USERS).child(friendId).child(Constants.DATABASE_CHILD_FRIEND_REQUESTS).child(currentUserId).removeValue()
            } else {
                Log.d(TAG, "No user logged in. Cannot delete friend request.")
            }
        }

        fun removeFriend(friendId: String) {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                database.child(Constants.DATABASE_CHILD_USERS).child(currentUserId).child(Constants.DATABASE_CHILD_FRIENDS).child(friendId).removeValue()
            } else {
                Log.d(TAG, "No user logged in. Cannot save remove friend.")
            }
        }

    }
}