package com.mdmbaku.mdmandroid.tabs

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.mdmbaku.mdmandroid.HomeActivity
import com.mdmbaku.mdmandroid.R
import com.mdmbaku.mdmandroid.data.AchievementItem
import com.mdmbaku.mdmandroid.data.WpPage
import com.mdmbaku.mdmandroid.tabs.adapters.AchievementsAdapter
import com.mdmbaku.mdmandroid.utils.IDataForFragment
import com.mdmbaku.mdmandroid.utils.Network
import io.realm.Realm
import org.json.JSONObject
import java.io.StringReader

private var gson: Gson = Gson()
private var mTeamPage: WpPage? = null

class TeamFragment : Fragment(), IDataForFragment {

    private lateinit var recyclerView: RecyclerView
    private var realm: Realm = Realm.getDefaultInstance()
    private lateinit var mTeamTextTitleTextView: TextView
    private lateinit var mAchievementsTextTitleTextView: TextView
    private lateinit var mTeamTextContentTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_team, container, false)
        recyclerView = rootView.findViewById(R.id.achievements_recyclerView) as RecyclerView
        mTeamTextTitleTextView = rootView.findViewById(R.id.team_text_title) as TextView
        mTeamTextContentTextView = rootView.findViewById(R.id.team_text_content) as TextView
        mAchievementsTextTitleTextView = rootView.findViewById(R.id.achievements_text_title) as TextView

        if (mTeamPage != null) {
            renderTeamPage()
        }

        if ((activity as HomeActivity).isNetworkAvailable()) {
            Network.getInstance().requestTeamPage(context!!, this, Network.Companion.RequestType.REQUEST_TEAM)
        }

        return rootView
    }

    override fun dataForFragment(jsonObject: JSONObject, requestType: Network.Companion.RequestType) {
        if (requestType == Network.Companion.RequestType.REQUEST_TEAM) {
            val teamPageStringReader = StringReader(jsonObject.toString())
            val teamPage: WpPage = gson.fromJson(teamPageStringReader, WpPage::class.java)

            try {
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(teamPage)
                realm.commitTransaction()
                updateTeamPage()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Realm error", e.message)
            }

            renderTeamPage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateTeamPage()
    }

    private fun updateTeamPage() {
        mTeamPage = realm.where(WpPage::class.java).equalTo("id",
                Network.Companion.WpPageId.TEAM.pageId).findFirst()
    }

    private fun renderTeamPage() {
        val teamPageContent = mTeamPage?.content?.renderedContent
        val achievementList = mutableListOf<AchievementItem>()

        if (!teamPageContent.isNullOrBlank()) {
            val beginningOfTeamTitle = teamPageContent!!.indexOf("<strong>") + 8
            val teamTitle = teamPageContent.substring(beginningOfTeamTitle,
                    teamPageContent.indexOf("</strong>"))

            val beginningOfAchievementTitle = teamPageContent.indexOf("<strong>", beginningOfTeamTitle) + 8
            val achievementsTitle = teamPageContent.substring(beginningOfAchievementTitle,
                    teamPageContent.indexOf("</strong>", beginningOfAchievementTitle))

            val teamContent = teamPageContent.substring(teamPageContent.indexOf("</strong></p>") + 14,
                    teamPageContent.indexOf("<p><strong>", beginningOfTeamTitle) - 1)

            val achievementsContent = teamPageContent.substring(
                    teamPageContent.indexOf("</strong>", beginningOfAchievementTitle) + 13, teamPageContent.length - 1)

            mTeamTextTitleTextView.text = teamTitle
            mAchievementsTextTitleTextView.text = achievementsTitle
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mTeamTextContentTextView.text = Html.fromHtml(teamContent, Html.FROM_HTML_MODE_LEGACY)
            } else {
                mTeamTextContentTextView.text = Html.fromHtml(teamContent)
            }

            val rawAchievementsList = achievementsContent.split("<p class=\"achievement\">")

            for (i in 1 until rawAchievementsList.size) {
                val achievementItem = rawAchievementsList[i]

                val endOfTitle = achievementItem.indexOf("</span>")
                val currentAchievementTitle = achievementItem.substring(
                        achievementItem.indexOf("<span class=\"achievement-content\">") + 33,
                        endOfTitle)

                val endOfAwarder = achievementItem.indexOf("</span>", endOfTitle + 7)
                val currentAchievementAwarder = achievementItem.substring(
                        achievementItem.indexOf("<span class=\"awarder\">") + 22, endOfAwarder)

                val endOfDate = achievementItem.indexOf("</span>", endOfAwarder + 7)
                val currentAchievementDate = achievementItem.substring(
                        achievementItem.indexOf("<span class=\"date\">") + 19,
                        endOfDate)

                achievementList.add(AchievementItem(currentAchievementTitle,
                        currentAchievementDate, currentAchievementAwarder))
            }

            val achievementsAdapter = AchievementsAdapter(achievementList)
            recyclerView.layoutManager = LinearLayoutManager(this@TeamFragment.activity)
            recyclerView.adapter = achievementsAdapter
        }
    }
}