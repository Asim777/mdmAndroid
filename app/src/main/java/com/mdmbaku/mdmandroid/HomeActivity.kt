package com.mdmbaku.mdmandroid

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.mdmbaku.mdmandroid.R.id.*
import com.mdmbaku.mdmandroid.tabs.*


class HomeActivity : AppCompatActivity() {

    private lateinit var mTabsPagerAdapter: TabsPagerAdapter
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<Toolbar>(toolbar)
        setSupportActionBar(toolbar)

        mDrawerLayout = findViewById(drawer_layout)
        val tabLayout = findViewById<TabLayout>(tabs)
        mViewPager = findViewById(home_viewpager)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = getString(R.string.mdm)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val navigationView: NavigationView = findViewById(nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()
            var tab: TabLayout.Tab? = null

            when (menuItem.itemId) {
                about_us -> {
                    tab = tabLayout.getTabAt(0)
                }
                portfolio -> {
                    tab = tabLayout.getTabAt(1)
                }
                team -> {
                    tab = tabLayout.getTabAt(2)
                }
                news -> {
                    tab = tabLayout.getTabAt(3)
                }
                contact_us -> {
                    tab = tabLayout.getTabAt(4)
                }
            }

            tab?.select()
            true
        }

        setupViewPager()
        tabLayout.setupWithViewPager(mViewPager)
    }

    private fun setupViewPager() {
        mTabsPagerAdapter = TabsPagerAdapter(supportFragmentManager)
        mTabsPagerAdapter.addFragment(AboutUsFragment())
        mTabsPagerAdapter.addFragment(PortfolioFragment())
        mTabsPagerAdapter.addFragment(TeamFragment())
        mTabsPagerAdapter.addFragment(NewsFragment())
        mTabsPagerAdapter.addFragment(ContactUsFragment())

        mViewPager.adapter = mTabsPagerAdapter
        mViewPager.offscreenPageLimit = 4
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

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}