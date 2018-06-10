package com.mdmbaku.mdmandroid.utils

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject


class Network {

    fun requestAboutUsPage(context: Context, fragment: IDataForFragment, requestType: RequestType) {
        requestWpPage(context, fragment, requestType, "2")
    }

    fun requestPortfolioPage(context: Context, fragment: IDataForFragment, requestType: RequestType) {
       requestWpPage(context, fragment, requestType, "321")
    }

    fun requestSinglePortfolioPage(context: Context, activity: IDataForActivity,
                                   requestType: RequestType, pageSlug: String) {
        requestWpPageBySlug(context, activity, requestType, "321", pageSlug)
    }

    private fun requestWpPageBySlug(context: Context, activity: IDataForActivity, requestType: Network.Companion.RequestType, s: String, pageSlug: String) {
        val responseListener = Response.Listener<JSONArray> { response ->
            if (response == null) return@Listener
            activity.dataForActivity(null, response, requestType)
        }

        val errorListener = Response.ErrorListener { error ->
            Log.d("ERROR", error.toString())
        }

        val requestUrl = BASE_URL + "pages?slug=" + pageSlug
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET,
                requestUrl, null, responseListener, errorListener)

        VolleySingleton.getInstance(context)?.addToRequestQueue(jsonArrayRequest)
    }

    private fun requestWpPage(context: Context, fragment: IDataForFragment, requestType: RequestType, pageId: String) {
        val responseListener = Response.Listener<JSONObject> { response ->
            if (response == null) return@Listener
            fragment.dataForFragment(response, requestType)
        }

        val errorListener = Response.ErrorListener { error ->
            Log.d("ERROR", error.toString())
        }

        val requestUrl = BASE_URL + "pages/" + pageId
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                requestUrl, null, responseListener, errorListener)

        VolleySingleton.getInstance(context)?.addToRequestQueue(jsonObjectRequest)
    }

    companion object {

        const val BASE_URL = "http://mdmbaku.com//wp-json/wp/v2/"

        @Volatile
        private lateinit var mInstance: Network

        enum class RequestType {
            REQUEST_ABOUT_US,
            REQUEST_NEWS,
            REQUEST_PORTFOLIO,
            REQUEST_CONTACT_US,
            REQUEST_SINGLE_PORTFOLIO
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