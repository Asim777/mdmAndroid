package com.mdmbaku.mdmandroid

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MenuItem
import com.mdmbaku.mdmandroid.R.id.*
import com.mdmbaku.mdmandroid.tabs.*
import kotlinx.android.synthetic.main.activity_home.*
import android.support.design.widget.TabLayout



class HomeActivity : AppCompatActivity() {

    private lateinit var mTabsPagerAdapter: TabsPagerAdapter
    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = getString(R.string.mdm)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)
        nav_view.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()
            var tab: TabLayout.Tab? = null

            when (menuItem.itemId) {
                about_us -> {
                    tab = tabs.getTabAt(0)
                }
                portfolio -> {
                    tab = tabs.getTabAt(1)
                }
                team -> {
                    tab = tabs.getTabAt(2)
                }
                news -> {
                    tab = tabs.getTabAt(3)
                }
                contact_us -> {
                    tab = tabs.getTabAt(4)
                }
            }

            tab?.select()
            true
        }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}