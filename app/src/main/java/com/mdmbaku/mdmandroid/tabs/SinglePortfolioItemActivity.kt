package com.mdmbaku.mdmandroid.tabs

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
import com.glide.slider.library.svg.GlideApp
import com.google.gson.Gson
import com.mdmbaku.mdmandroid.R
import com.mdmbaku.mdmandroid.data.PortfolioItem
import com.mdmbaku.mdmandroid.data.PortfolioSingleItem
import com.mdmbaku.mdmandroid.utils.IDataForActivity
import com.mdmbaku.mdmandroid.utils.Network
import org.json.JSONArray
import org.json.JSONObject
import java.io.StringReader


private val gson: Gson = Gson()

class SinglePortfolioItemActivity : AppCompatActivity(), IDataForActivity {

    lateinit var mPortfolioImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_single_portfolio_item)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        mPortfolioImageView = findViewById(R.id.portfolio_image)
        setSupportActionBar(toolbar)

        val portfolioItem = intent.getParcelableExtra<PortfolioItem>(PORTFOLIO_ITEM)
        val portfolioItemTitle = intent.getStringExtra(PORTFOLIO_ITEM_TITLE)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = portfolioItemTitle
        }

        Network.getInstance().requestSinglePortfolioPage(this, this,
                Network.Companion.RequestType.REQUEST_SINGLE_PORTFOLIO, portfolioItem.slug)

    }

    override fun dataForActivity(jsonObj: JSONObject?, jsonArray: JSONArray?, requestType: Network.Companion.RequestType) {
        if (requestType == Network.Companion.RequestType.REQUEST_SINGLE_PORTFOLIO) {

            if (jsonArray != null) {
                val singlePortfolioItemContentStringReader = StringReader(jsonArray.get(0).toString())
                val portfolioSingleItem = gson.fromJson(singlePortfolioItemContentStringReader, PortfolioSingleItem::class.java)

                val portfolioContent = portfolioSingleItem.content?.renderedContent
                val portfolioSingleImageLink = portfolioContent?.substring(portfolioContent.indexOf("768w,") + 6,
                        portfolioContent.indexOf("1024w") - 1)
                GlideApp.with(applicationContext)
                        .load(portfolioSingleImageLink)
                        .into(mPortfolioImageView)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}