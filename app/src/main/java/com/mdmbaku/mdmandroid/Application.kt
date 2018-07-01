package com.mdmbaku.mdmandroid

import android.content.Context
import android.graphics.Typeface
import android.support.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration

class ApplicationClass : MultiDexApplication() {

    companion object {
        private var mInstance: ApplicationClass? = null
        fun getAppContext(): Context = mInstance!!.applicationContext
        fun getAppInstance() : ApplicationClass? = mInstance!!
    }
    override fun onCreate() {
        super.onCreate()

        mInstance = this

        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder()
                .name("mdm.realm")
                .schemaVersion(0)
                .build()
        Realm.setDefaultConfiguration(realmConfig)
    }

    fun getRegularTypeface() : Typeface{
        return Typeface.createFromAsset(assets, "fonts/Arimo-Regular.ttf" )
    }

    fun getBoldTypeface() : Typeface{
        return Typeface.createFromAsset(assets, "fonts/Arimo-Bold.ttf" )
    }

    fun getItalicTypeface() : Typeface{
        return Typeface.createFromAsset(assets, "fonts/Arimo-Italic.ttf" )
    }


}