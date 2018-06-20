package com.mdmbaku.mdmandroid.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.Required

open class PortfolioItem(
        @Required
        open var title: String = "",

        @Required
        open var thumbnail: String = "",

        @Required
        open var link: String = "",

        @Required
        open var slug: String = ""
) : RealmObject(), Parcelable {
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