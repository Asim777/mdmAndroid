package com.mdmbaku.mdmandroid

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.mdmbaku.mdmandroid.tabs.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var mTabsPagerAdapter: TabsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

         val actionBar = supportActionBar ?: return
         actionBar.title = getString(R.string.about_us)
         actionBar.navigationMode = ActionBar.NAVIGATION_MODE_TABS

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        setupViewPager()
        tabs.setupWithViewPager(home_viewpager)

        /*val tabListener = object : ActionBar.TabListener {
            override fun onTabReselected(tab: ActionBar.Tab?, ft: FragmentTransaction?) {
            }

            override fun onTabUnselected(tab: ActionBar.Tab?, ft: FragmentTransaction?) {
            }

            override fun onTabSelected(tab: ActionBar.Tab, ft: FragmentTransaction?) {
                home_viewpager.currentItem = tab.position
            }
        }*/

        /*for (i in 0 until 5) {
            actionBar.addTab(actionBar.newTab()
                    *//*.setTabListener(tabListener)*//*
                    .setText(tabNames[i]))
        }*/
    }

    private fun setupViewPager() {
        mTabsPagerAdapter = TabsPagerAdapter(supportFragmentManager)
        mTabsPagerAdapter.addFragment(AboutUsFragment())
        mTabsPagerAdapter.addFragment(PortfolioFragment())
        mTabsPagerAdapter.addFragment(TeamFragment())
        mTabsPagerAdapter.addFragment(NewsFragment())
        mTabsPagerAdapter.addFragment(ContactUsFragment())

        home_viewpager.adapter = mTabsPagerAdapter
        home_viewpager.offscreenPageLimit = 4
    }

}