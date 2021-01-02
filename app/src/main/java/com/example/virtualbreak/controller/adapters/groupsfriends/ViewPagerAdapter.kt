package com.example.virtualbreak.controller.adapters.groupsfriends

import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.virtualbreak.view.view_fragments.groupsfriends.Groups_friendlist_fragment
import com.example.virtualbreak.view.view_fragments.groupsfriends.Groups_grouplist_fragment


class ViewPagerAdapter(fragmentActivity: FragmentActivity?) :
    FragmentStateAdapter(fragmentActivity!!) {

    private val fragments:ArrayList<Fragment> = arrayListOf(
        Groups_grouplist_fragment(),
        Groups_friendlist_fragment()
    )

    override fun getItemCount(): Int {
       return fragments.size
    }


    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }


}