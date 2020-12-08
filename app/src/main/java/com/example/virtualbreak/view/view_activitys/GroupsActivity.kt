package com.example.virtualbreak.view.view_activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.GroupsStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_groups.*

class GroupsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        initViewPager2WithFragments()
    }

    private fun initViewPager2WithFragments(){

        val adapter = GroupsStateAdapter(supportFragmentManager, lifecycle)
        groups_view_Pager.adapter = adapter

        //Scrolling?
        //groups_view_Pager.orientation = ViewPager2.ORIENTATION_VERTICAL

        val tabNames:Array<String> = arrayOf("Groups", "Friends")
        TabLayoutMediator(groups_tab_layout, groups_view_Pager){tab, position ->
            tab.text = tabNames[position]
        }.attach()

    }
}