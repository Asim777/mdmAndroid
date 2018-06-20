package com.mdmbaku.mdmandroid.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class PortfolioSingleItem(
        @SerializedName("content")
        open var content: WpPageContent? = null
) : RealmObject()