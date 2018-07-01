package com.mdmbaku.mdmandroid.tabs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mdmbaku.mdmandroid.ApplicationClass
import com.mdmbaku.mdmandroid.R

class NewsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView : View = inflater.inflate(R.layout.fragment_news, container, false)
        val comingSoonTextView = rootView.findViewById(R.id.coming_soon_news) as TextView
        comingSoonTextView.typeface = ApplicationClass.getAppInstance()?.getBoldTypeface()

        return rootView
    }
}
