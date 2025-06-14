package com.example.messageapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.messageapp.fragments.ContactsFragment
import com.example.messageapp.fragments.MessagesFragment

class ViewPagerAdapter(
    private val tabs : List<String>,
    lifecycle: Lifecycle,
    fragmentManager: FragmentManager
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return tabs.size
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            1 -> return ContactsFragment()
        }
        return MessagesFragment()
    }
}