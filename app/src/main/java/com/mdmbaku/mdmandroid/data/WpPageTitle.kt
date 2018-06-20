package com.mdmbaku.mdmandroid.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.Required

open class WpPageTitle(
        @Required
        @SerializedName("rendered") open var renderedTitle: String = ""
) : RealmObject()