package com.mdmbaku.mdmandroid

import android.content.Context
import android.support.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration

class ApplicationClass : MultiDexApplication() {

    companion object {
        private var instance: ApplicationClass? = null
        fun getAppContext(): Context = instance!!.applicationContext
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder()
                .name("mdm.realm")
                .schemaVersion(0)
                .build()
        Realm.setDefaultConfiguration(realmConfig)
    }
}