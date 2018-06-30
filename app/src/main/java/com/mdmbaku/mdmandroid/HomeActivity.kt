package com.mdmbaku.mdmandroid

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import com.mdmbaku.mdmandroid.R.id.*
import com.mdmbaku.mdmandroid.tabs.*
import kotlinx.android.synthetic.main.navigation_menu.*

const val FACEBOOK_URL = "https://www.facebook.com/mdmgroup.co/"
const val TWITTER_URL = "https://twitter.com/m_d_m_llc"
const val LINKEDIN_URL = "https://www.linkedin.com/company/27157796/"
const val INSTAGRAM_URL = "https://www.instagram.com/mdm_llc/"


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

        setupNavigationView(tabLayout)
        setupViewPager()
        tabLayout.setupWithViewPager(mViewPager)
    }

    private fun setupNavigationView(tabLayout: TabLayout) {
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        navigationView.setCustomNavigationItemSelectedListener(object : CustomNavigationItemSelectedListener() {
            override fun onNavigationItemSelected(item: TextView) {
                // set item as selected to persist highlight
                deselectAllMenuItems()
                item.select(true)
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers()
                var tab: TabLayout.Tab? = null

                when (item.id) {
                    menu_about_us.id -> {
                        tab = tabLayout.getTabAt(0)
                    }
                    menu_portfolio.id -> {
                        tab = tabLayout.getTabAt(1)
                    }
                    menu_team.id -> {
                        tab = tabLayout.getTabAt(2)
                    }
                    menu_news.id -> {
                        tab = tabLayout.getTabAt(3)
                    }
                    menu_contact_us.id -> {
                        tab = tabLayout.getTabAt(4)
                    }
                    menu_my_account.id -> {
                        tab = tabLayout.getTabAt(5)
                    }
                }
                tab?.select()
            }

            private fun deselectAllMenuItems() {
                val navMenu = nav_view.findViewById(R.id.navigation_menu) as LinearLayout
                for (i in 0 until navMenu.childCount) {
                    (navMenu.getChildAt(i) as TextView).select(false)
                }
            }
        })

        setupSocialNetworkLinks()
    }

    private fun setupSocialNetworkLinks() {
        facebook.setOnClickListener {
            openBrowserPage(FACEBOOK_URL)
        }

        twitter.setOnClickListener {
            openBrowserPage(TWITTER_URL)
        }

        linkedin.setOnClickListener {
            openBrowserPage(LINKEDIN_URL)
        }

        instagram.setOnClickListener {
            openBrowserPage(INSTAGRAM_URL)
        }
    }

    private fun openBrowserPage(url: String) {
        val myAccountIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(myAccountIntent)
    }

    private fun setupViewPager() {
        mTabsPagerAdapter = TabsPagerAdapter(supportFragmentManager)
        mTabsPagerAdapter.addFragment(AboutUsFragment())
        mTabsPagerAdapter.addFragment(PortfolioFragment())
        mTabsPagerAdapter.addFragment(TeamFragment())
        mTabsPagerAdapter.addFragment(NewsFragment())
        mTabsPagerAdapter.addFragment(ContactUsFragment())
        mTabsPagerAdapter.addFragment(MyAccountFragment())

        mViewPager.adapter = mTabsPagerAdapter
        mViewPager.offscreenPageLimit = 5
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

fun NavigationView.setCustomNavigationItemSelectedListener(listener: CustomNavigationItemSelectedListener) {
    val navMenu = this.findViewById(R.id.navigation_menu) as LinearLayout
    for (i in 0 until navMenu.childCount) {
        navMenu.getChildAt(i).setOnClickListener { listener.onNavigationItemSelected(it as TextView)}
    }
}

fun TextView.select(selected: Boolean) {
    if (selected) {
        this.setTextColor(android.support.v4.content.ContextCompat.getColor(
                com.mdmbaku.mdmandroid.ApplicationClass.getAppContext(), R.color.colorAccent))
    } else {
        this.setTextColor(android.support.v4.content.ContextCompat.getColor(
                com.mdmbaku.mdmandroid.ApplicationClass.getAppContext(), R.color.white))
    }
}


abstract class CustomNavigationItemSelectedListener {
    abstract fun onNavigationItemSelected(item: TextView)
}