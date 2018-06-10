
package com.mdmbaku.mdmandroid.utils

import org.json.JSONObject

interface IDataForFragment {

    fun dataForFragment(jsonObject: JSONObject, requestType: Network.Companion.RequestType)
}
