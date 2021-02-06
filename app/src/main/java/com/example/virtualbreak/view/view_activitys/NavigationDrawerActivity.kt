package com.example.virtualbreak.view.view_activitys

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.FCMService
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.Roomtype
import com.example.virtualbreak.view.view_activitys.breakroom.BreakRoomActivity
import com.example.virtualbreak.view.view_activitys.breakroom.BreakroomWidgetService
import com.example.virtualbreak.view.view_fragments.singlegroup.SingleGroupViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

/**
 * Activity with a navigation drawer on the left to navigate to other app fragments
 * uses Navigation component to switch between fragments
 * 1) add existing fragment as destination to (res/navigation/mobile_navigation) in Design view and
 * 2) In menu/activity_main_drawer add item with same ID as fragment in 1)
 * 3) NavController automatically handles switching the fragments when menu item are clicked
 *
 */
class NavigationDrawerActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val TAG = "NavigationDrawerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "onCreate")

        super.onCreate(savedInstanceState)

        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            //if no user logged in, intent to MainActivity
        }
        PullData.attachListenerToCurrentUser()

        PullData.pullAndSaveOwnUserName()
        FCMService.addFCMTokenListener()

        //Check if user is currently in a Breakroom
        Log.d("CHECK", "roomid: ${SharedPrefManager.instance.getRoomId()}")
        if (SharedPrefManager.instance.getRoomId() != null) {
            getCurrentRoom()
        }

        setContentView(R.layout.activity_navigationdrawer)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_myprofile/*, R.id.nav_logout*/
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onStart() {
        super.onStart()

    }

    //catches case when user logs out and then presses back
    override fun onResume() {
        super.onResume()
        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            //if no user logged in, intent to MainActivity
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> showSettingsDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showSettingsDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.settings_dialog)
        dialog.show()

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "OnDestroy")
        stopService(Intent(this, BreakroomWidgetService::class.java))
    }


    private fun getCurrentRoom() {
        PullData.database.child(Constants.DATABASE_CHILD_ROOMS)
            .child(SharedPrefManager.instance.getRoomId() ?: "")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val pulledRoom = dataSnapshot.getValue<Room>()
                    Log.d("CHECK", "Pulled currentRoom $pulledRoom")
                    // if (pulledRoom == null) return
                    PullData.currentRoom = pulledRoom
                    openBreakroom()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("NAV", error.message)
                }
            })
    }

    private fun openBreakroom() {

        Log.d(TAG, "go to current Breakroom")

        val intent = Intent(this@NavigationDrawerActivity, BreakRoomActivity::class.java)

        if (PullData.currentRoom?.type == Roomtype.GAME) {
            intent.putExtra(Constants.GAME_ID, PullData.currentRoom?.gameId)
        } else {
            Log.w(TAG, "Room ID is null, can't open Breakroom")
        }

        intent.putExtra(Constants.USER_NAME, SharedPrefManager.instance.getUserName())
        intent.putExtra(Constants.ROOM_TYPE, PullData.currentRoom?.type?.dbStr)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this@NavigationDrawerActivity.startActivity(intent)

    }
}