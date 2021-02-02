package com.example.virtualbreak.view.view_activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Roomtype
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.view.view_activitys.breakroom.BreakRoomActivity
import com.example.virtualbreak.view.view_fragments.singlegroup.SingleGroupViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var signupBtn: Button
    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPrefManager.instance.init(applicationContext)

        // If user is logged in, redirect to navigation drawer activity
        if (Firebase.auth.currentUser != null) {
            // Save userId in shared preferences
            SharedPrefManager.instance.saveUserId(Firebase.auth.currentUser!!.uid)
            //Check if user is currently  in break if yes send to Break if not send to NavigationDrawerActivity
            if (SharedPrefManager.instance.getRoomId() != null) {
                startActivity(Intent(this, NavigationDrawerActivity::class.java))
            }
            startActivity(Intent(this, NavigationDrawerActivity::class.java))
            return
        }

        setContentView(R.layout.activity_main)

        signupBtn = findViewById(R.id.signupButton)
        signupBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        loginBtn = findViewById(R.id.loginButton)
        loginBtn.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

    }

//    private fun openBreakroom() {
//        PullData.currentUser.value?.status?.let { it ->
//            if(SharedPrefManager.instance.getRoomId() == null || "".equals(SharedPrefManager.instance.getRoomId())){ //only save status before going in breakroom if about to enter new room (not reenter)
//                SharedPrefManager.instance.saveCurrentStatus(
//                    it
//                )
//            }
//        }
//
//        Log.d(TAG, "create $roomtype Breakroom")
//
//        val intent = Intent(this@MainActivity, BreakRoomActivity::class.java)
//                    if(roomtype == Roomtype.GAME){
//                        val gameId = PushData.createGame(roomId)
//                        intent.putExtra(Constants.GAME_ID, gameId)
//                    }
//
//        intent.putExtra(Constants.USER_NAME, userName)
//        intent.putExtra(Constants.ROOM_TYPE, roomtype.dbStr)
//        this@MainActivity.startActivity(intent)
//
//    }


    /*override fun onBackPressed() { //not needed, because redirect NavigationDrawerActivity when user not logged in
        // Do nothing, to prevent going back to NavigationDrawerActivity after Logout
    }*/

}