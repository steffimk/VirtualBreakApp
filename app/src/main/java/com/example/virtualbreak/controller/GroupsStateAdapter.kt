package com.example.virtualbreak.controller

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.virtualbreak.view.view_fragments.Groups_friendlist_fragment
import com.example.virtualbreak.view.view_fragments.Groups_grouplist_fragment


class GroupsStateAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments:ArrayList<Fragment> = arrayListOf(
        Groups_grouplist_fragment(),
        Groups_friendlist_fragment()
    )

    override fun getItemCount(): Int {
        return  fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }


}