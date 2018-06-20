package com.mdmbaku.mdmandroid.utils

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.mdmbaku.mdmandroid.tabs.ContactUsFragment
import com.mdmbaku.mdmandroid.tabs.TeamFragment
import org.json.JSONArray
import org.json.JSONObject


class Network {

    fun requestAboutUsPage(context: Context, fragment: IDataForFragment, requestType: RequestType) {
        requestWpPage(context, fragment, requestType, WpPageId.ABOUT_US.pageId.toString())
    }

    fun requestPortfolioPage(context: Context, fragment: IDataForFragment, requestType: RequestType) {
       requestWpPage(context, fragment, requestType, WpPageId.PORTFOLIO.pageId.toString())
    }

    fun requestSinglePortfolioPage(context: Context, activity: IDataForActivity,
                                   requestType: RequestType, pageSlug: String) {
        requestWpPageBySlug(context, activity, requestType, pageSlug)
    }

    fun requestTeamPage(context: Context, fragment: TeamFragment, requestType: RequestType) {
        requestWpPage(context, fragment, requestType,  WpPageId.TEAM.pageId.toString())
    }

    fun requestContactUsPage(context: Context, fragment: ContactUsFragment, requestType: RequestType) {
        requestWpPage(context, fragment, requestType, WpPageId.CONTACT_US.pageId.toString())
    }

    private fun requestWpPageBySlug(context: Context, activity: IDataForActivity,
                                    requestType: Network.Companion.RequestType, pageSlug: String) {
        val responseListener = Response.Listener<JSONArray> { response ->
            if (response == null) return@Listener
            activity.dataForActivity(null, response, requestType)
        }

        val errorListener = Response.ErrorListener { error ->
            Log.d("ERROR", error.toString())
        }

        val requestUrl = BASE_URL + "pages?slug=" + pageSlug
        Log.v("Network", requestUrl)
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET,
                requestUrl, null, responseListener, errorListener)

        VolleySingleton.getInstance(context)?.addToRequestQueue(jsonArrayRequest)
    }

    private fun requestWpPage(context: Context, fragment: IDataForFragment, requestType: RequestType, pageId: String) {
        val responseListener = Response.Listener<JSONObject> { response ->
            if (response == null) return@Listener
            Log.v("Network", response.toString())
            fragment.dataForFragment(response, requestType)
        }

        val errorListener = Response.ErrorListener { error ->
            Log.d("ERROR", error.toString())
        }

        val requestUrl = BASE_URL + "pages/" + pageId
        Log.v("Network", requestUrl)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                requestUrl, null, responseListener, errorListener)

        VolleySingleton.getInstance(context)?.addToRequestQueue(jsonObjectRequest)
    }

    companion object {

        const val BASE_URL = "http://mdmbaku.com/wp-json/wp/v2/"

        @Volatile
        private lateinit var mInstance: Network
        enum class RequestType {
            REQUEST_ABOUT_US,
            REQUEST_NEWS,
            REQUEST_PORTFOLIO,
            REQUEST_CONTACT_US,
            REQUEST_SINGLE_PORTFOLIO,
            REQUEST_TEAM
        }

        enum class WpPageId(val pageId: Int) {
            ABOUT_US(2),
            NEWS(0),
            PORTFOLIO(321),
            CONTACT_US(766),
            TEAM(761)
        }

        fun getInstance(): Network {
            synchronized(Network::class.java) {
                mInstance = Network()
            }

            return mInstance
        }

    }

    fun cancelAll(context: Context) {
        VolleySingleton.getInstance(context)?.cancelAll()
    }
}