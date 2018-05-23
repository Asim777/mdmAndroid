package com.mdmbaku.mdmandroid

import android.app.Application
import android.content.Context

class ApplicationClass: Application() {

    init {
        instance = this
    }


    companion object {
        private var instance : ApplicationClass? = null
        fun getAppContext(): Context = instance!!.applicationContext
    }
}