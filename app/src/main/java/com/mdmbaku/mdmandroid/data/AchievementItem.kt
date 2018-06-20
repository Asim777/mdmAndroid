package com.mdmbaku.mdmandroid.data

import io.realm.RealmObject
import io.realm.annotations.Required

open class AchievementItem(
        @Required
        open var content: String = "",

        @Required
        open var date: String = "",

        @Required
        open var awarder: String = ""
) : RealmObject()