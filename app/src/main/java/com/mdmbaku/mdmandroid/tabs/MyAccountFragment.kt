package com.mdmbaku.mdmandroid.tabs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mdmbaku.mdmandroid.R

const val MY_ACCOUNT_URL = "http://213.172.90.226/config/Security/mdm-login.php"

class MyAccountFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView : View = inflater.inflate(R.layout.fragment_my_account, container, false)
        val imTeamMember = rootView.findViewById(R.id.im_team_member) as TextView

        val spannableText = SpannableString(imTeamMember.text)
        spannableText.setSpan(UnderlineSpan(), 0, spannableText.length, 0)
        imTeamMember.text = spannableText
        imTeamMember.setOnClickListener {
            val myAccountIntent = Intent(Intent.ACTION_VIEW, Uri.parse(MY_ACCOUNT_URL))
            startActivity(myAccountIntent)
        }

        return rootView
    }
}
