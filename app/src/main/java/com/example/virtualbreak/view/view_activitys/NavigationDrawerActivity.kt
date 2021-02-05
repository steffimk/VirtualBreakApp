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
import com.example.virtualbreak.view.view_activitys.breakroom.BreakroomWidgetService
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
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


        if (SharedPrefManager.instance.getRoomId() != null) {
            //Open the widget to signal user is still in break
            openWidget()
        }

        PullData.pullAndSaveOwnUserName()
        FCMService.addFCMTokenListener()

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

    //catches case when user logs out and then presses back
    override fun onResume() {
        super.onResume()
        if(Firebase.auth.currentUser == null){
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
        when(item.itemId){
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
        stopService(Intent(this, BreakroomWidgetService::class.java))
    }


    private fun openWidget() {
        Log.d(TAG, "openwidget")
        if (Settings.canDrawOverlays(this)) {
            if (SharedPrefManager.instance.getIsWidgetAllowedtoOpen()) {
                //if(!SharedPrefManager.instance.getIsWidgetOpen()) {
                //Log.d(TAG, room?.description + room?.type?.dbStr)
                SharedPrefManager.instance.saveIsWidgetAllowedtoOpen(false)
                Log.d("Check", "openWidget" + SharedPrefManager.instance.getIsWidgetAllowedtoOpen())
                val intent = Intent(this, BreakroomWidgetService::class.java)
                intent.putExtra(Constants.ROOM_NAME, "test")
                intent.putExtra(Constants.ROOM_TYPE, "test")
                intent.putExtra(Constants.USER_NAME, "me")
                //intent.putExtra(Constants.GAME_ID, gameId)
                startService(intent)
            }
            //}
        } else {
            askPermission()
            Toast.makeText(
                this,
                "You need System Alert Window Permission to do this",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun askPermission() {
        Log.d(TAG, "askPermission")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
            );
            startActivityForResult(intent, 123);
        }
    }
}