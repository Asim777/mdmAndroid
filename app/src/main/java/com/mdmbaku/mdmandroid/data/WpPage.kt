package com.mdmbaku.mdmandroid.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class WpPage(
        @PrimaryKey
        @SerializedName("id") open var id: Int = 0,

        @SerializedName("title") open var title: WpPageTitle? = null,

        @SerializedName("content") open var content: WpPageContent? = null
) : RealmObject()