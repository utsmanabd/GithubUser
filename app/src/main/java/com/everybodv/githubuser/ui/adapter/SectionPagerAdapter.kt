package com.everybodv.githubuser.ui.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.everybodv.githubuser.ui.detail.FollowsFragment

class SectionPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    var userName : String =""

    override fun createFragment(position: Int): Fragment {
        val fragment = FollowsFragment()
        fragment.arguments = Bundle().apply {
            putInt(FollowsFragment.ARG_POSITION, position+1)
            putString(FollowsFragment.ARG_USERNAME, userName)
        }
        return fragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}