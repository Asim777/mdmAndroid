package com.mdmbaku.mdmandroid.tabs

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.mdmbaku.mdmandroid.HomeActivity
import com.mdmbaku.mdmandroid.R
import com.mdmbaku.mdmandroid.data.PortfolioItem
import com.mdmbaku.mdmandroid.data.WpPage
import com.mdmbaku.mdmandroid.tabs.adapters.PortfolioAdapter
import com.mdmbaku.mdmandroid.utils.IDataForFragment
import com.mdmbaku.mdmandroid.utils.Network
import io.realm.Realm
import org.json.JSONObject
import java.io.StringReader

const val PORTFOLIO_ITEM = "portfolio_item"
const val PORTFOLIO_ITEM_TITLE = "portfolio_item_title"
private var gson: Gson = Gson()
private var mPortfolioPage: WpPage? = null


class PortfolioFragment : Fragment(), IDataForFragment {
    private lateinit var recyclerView: RecyclerView
    private var realm: Realm = Realm.getDefaultInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_portfolio, container, false)
        recyclerView = rootView.findViewById(R.id.portfolio_recyclerView) as RecyclerView

        if (mPortfolioPage != null) {
            renderPortfolioList()
        }

        if ((activity as HomeActivity).isNetworkAvailable()) {
            Network.getInstance().requestPortfolioPage(context!!, this, Network.Companion.RequestType.REQUEST_PORTFOLIO)
        }

        return rootView
    }

    override fun dataForFragment(jsonObject: JSONObject, requestType: Network.Companion.RequestType) {

        if (requestType == Network.Companion.RequestType.REQUEST_PORTFOLIO) {
            val portfolioPageStringReader = StringReader(jsonObject.toString())
            val portfolioPage: WpPage = gson.fromJson(portfolioPageStringReader, WpPage::class.java)

            try {
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(portfolioPage)
                realm.commitTransaction()
                updatePortfolioPage()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Realm error", e.message)
            }

            renderPortfolioList()
        }
    }

    private fun updatePortfolioPage() {
        mPortfolioPage = realm.where(WpPage::class.java).equalTo("id",
                Network.Companion.WpPageId.PORTFOLIO.pageId).findFirst()

    }

    private fun renderPortfolioList() {
        val portfolioList: MutableList<PortfolioItem> = mutableListOf()
        val portfolioContent = mPortfolioPage?.content?.renderedContent
        if (portfolioContent != null) {
            val rawPortfolioItemList = portfolioContent.split("</h3>\n<hr />")

            for (i in 1 until rawPortfolioItemList.size) {
                val portfolioItem = rawPortfolioItemList[i]
                val currentPortfolioImageUrl = portfolioItem.substring(portfolioItem.indexOf("300w,") + 6,
                        portfolioItem.indexOf(" 768w,"))

                val beginningOfTitle = portfolioItem.indexOf("/\">")
                val endOfTitle = portfolioItem.indexOf("</a>")
                val currentPortfolioTitle = portfolioItem.substring(portfolioItem.indexOf("/\">", beginningOfTitle + 1) + 3,
                        portfolioItem.indexOf("</a>", endOfTitle + 1))

                val currentPortfolioItemLink = portfolioItem.substring(portfolioItem.indexOf("<a href=") + 9,
                        portfolioItem.indexOf("><img") - 1)

                val startOfSlug = currentPortfolioItemLink.indexOf(".com/")
                val currentPortfolioItemSlug = currentPortfolioItemLink.substring(startOfSlug + 5,
                        currentPortfolioItemLink.indexOf("/", startOfSlug + 5))

                portfolioList.add(PortfolioItem(currentPortfolioTitle, currentPortfolioImageUrl,
                        currentPortfolioItemLink, currentPortfolioItemSlug))
            }


            val itemDecoration = DividerItemDecoration(this@PortfolioFragment.activity,
                    DividerItemDecoration.VERTICAL)
            recyclerView.addItemDecoration(itemDecoration)
            val portfolioAdapter = PortfolioAdapter(portfolioList)
            recyclerView.layoutManager = LinearLayoutManager(this@PortfolioFragment.activity)
            recyclerView.adapter = portfolioAdapter

            portfolioAdapter.setOnItemClickListener(object : PortfolioAdapter.OnItemClickListener {
                override fun onItemClick(view: View, selectedPortfolioItem: PortfolioItem) {
                    val singlePortfolioItemIntent = Intent(this@PortfolioFragment.activity,
                            SinglePortfolioItemActivity::class.java)
                    singlePortfolioItemIntent.putExtra(PORTFOLIO_ITEM, selectedPortfolioItem)
                    singlePortfolioItemIntent.putExtra(PORTFOLIO_ITEM_TITLE, selectedPortfolioItem.title)
                    startActivity(singlePortfolioItemIntent)
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updatePortfolioPage()
    }
}
