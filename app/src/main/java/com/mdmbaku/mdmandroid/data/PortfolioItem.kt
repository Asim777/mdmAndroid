package com.mdmbaku.mdmandroid.data

import android.os.Parcel
import android.os.Parcelable

data class PortfolioItem(
        val title: String,
        val thumbnail: String,
        val link: String,
        val slug: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(thumbnail)
        parcel.writeString(link)
        parcel.writeString(slug)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PortfolioItem> {
        override fun createFromParcel(parcel: Parcel): PortfolioItem {
            return PortfolioItem(parcel)
        }

        override fun newArray(size: Int): Array<PortfolioItem?> {
            return arrayOfNulls(size)
        }
    }
}