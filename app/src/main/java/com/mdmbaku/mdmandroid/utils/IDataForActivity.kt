package com.mdmbaku.mdmandroid.utils

import org.json.JSONArray
import org.json.JSONObject

interface IDataForActivity {
    fun dataForActivity(jsonObj: JSONObject?, jsonArray: JSONArray?, requestType: Network.Companion.RequestType)
}
