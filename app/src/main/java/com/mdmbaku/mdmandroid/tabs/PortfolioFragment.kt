package com.mdmbaku.mdmandroid.tabs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mdmbaku.mdmandroid.R

class PortfolioFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView : View = inflater.inflate(R.layout.fragment_portfolio, container, false)

        return rootView
    }
}
