package com.example.virtualbreak.view.view_activitys

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Roomtype
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.model.User
import com.google.android.material.navigation.NavigationView

/**
 * Activity with a navigation drawer on the left to navigate to other app fragments
 * uses Navigation component to switch between fragments
 * 1) add existing fragment as destination to (res/navigation/mobile_navigation) in Design view and
 * 2) In menu/activity_main_drawer add item with same ID as fragment in 1)
 * 3) NavController automatically handles switching the fragments when menu item are clicked
 *
 */
class NavigationDrawerActivity : AppCompatActivity(){

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //The following is for testing the data base --------------------------------
//        val groupId = PushData.saveGroup("Neue Gruppe", null)
//        if (groupId != null) {
//            val roomId = PushData.saveRoom(groupId, Roomtype.GAME)
//        }
        //--------------------------------------------------------------------------

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}