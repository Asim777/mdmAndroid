package com.mdmbaku.mdmandroid.tabs

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.glide.slider.library.svg.GlideApp
import com.google.gson.Gson
import com.mdmbaku.mdmandroid.R
import com.mdmbaku.mdmandroid.data.PortfolioItem
import com.mdmbaku.mdmandroid.data.PortfolioSingleItem
import com.mdmbaku.mdmandroid.utils.IDataForActivity
import com.mdmbaku.mdmandroid.utils.Network
import kotlinx.android.synthetic.main.activity_single_portfolio_item.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.StringReader

private val gson: Gson = Gson()

class SinglePortfolioItemActivity : AppCompatActivity(), IDataForActivity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_single_portfolio_item)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
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

                val portfolioContent = portfolioSingleItem.content.renderedContent
                val portfolioSingleImageLink = portfolioContent.substring(portfolioContent.indexOf("768w,") + 6,
                        portfolioContent.indexOf("1024w") -1)
                GlideApp.with(this)
                        .load(portfolioSingleImageLink)
                        .into(portfolio_image)


                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    portfolio_content.text = Html.fromHtml(portfolioContent, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    portfolio_content.text = Html.fromHtml(portfolioContent)
                }*/
            }
        }
    }
}