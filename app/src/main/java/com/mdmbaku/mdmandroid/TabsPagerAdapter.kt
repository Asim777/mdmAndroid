package com.mdmbaku.mdmandroid

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.util.ArrayList

private const val ARG_POSITION = "position"

class TabsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val context : Context = ApplicationClass.getAppContext()
    private var fragmentList = mutableListOf<Fragment>()

    override fun getItem(position: Int): Fragment {
        val fragment = fragmentList[position]
        fragment.arguments = Bundle().apply { putInt(ARG_POSITION, position) }
        return fragment
    }

    override fun getCount() = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getStringArray(R.array.tab_names)[position]
    }

    internal fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }

}