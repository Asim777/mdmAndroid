package com.mdmbaku.mdmandroid.data

import com.google.gson.annotations.SerializedName

data class WpPage(
        @SerializedName("id") val id: Int,
        @SerializedName("title") val title: WpPageTitle,
        @SerializedName("content") val content: WpPageContent
)