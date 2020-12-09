package com.example.virtualbreak.controller.adapters

import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.virtualbreak.view.view_fragments.Groups_friendlist_fragment
import com.example.virtualbreak.view.view_fragments.Groups_grouplist_fragment


class ViewPagerAdapter(fragmentActivity: FragmentActivity?) :
    FragmentStateAdapter(fragmentActivity!!) {

    private val fragments:ArrayList<Fragment> = arrayListOf(
        Groups_grouplist_fragment(),
        Groups_friendlist_fragment()
    )

    override fun getItemCount(): Int {
       return fragments.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }


}

/*
class GroupsStateAdapter(fragmentManager: FragmentManager?, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager!!, lifecycle) {

    private val fragments:ArrayList<Fragment> = arrayListOf(
        Groups_grouplist_fragment(),
        Groups_friendlist_fragment()
    )

    override fun getItemCount(): Int {
        Log.i("M_FragmentitemCount", fragments.size.toString())
        return  fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        Log.i("M_FragmentitemCount", fragments[position].toString())
        return fragments[position]
    }


}

 */