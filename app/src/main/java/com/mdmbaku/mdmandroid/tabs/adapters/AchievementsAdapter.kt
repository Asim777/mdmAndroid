package com.mdmbaku.mdmandroid.tabs.adapters

import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mdmbaku.mdmandroid.R
import com.mdmbaku.mdmandroid.data.AchievementItem

class AchievementsAdapter(private val achievementItems : List<AchievementItem>) :
        RecyclerView.Adapter<AchievementsAdapter.VHItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHItem{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.achievement_item, parent, false)
        return VHItem(v)
    }

    override fun getItemCount(): Int {
        return achievementItems.size
    }

    fun getItem(position: Int): AchievementItem {
        return achievementItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: VHItem, position: Int) {
        holder.title.text = getItem(position).content
        holder.awarder.text = getItem(position).awarder
        holder.date.text = getItem(position).date

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.title.text = Html.fromHtml(getItem(position).content, Html.FROM_HTML_MODE_LEGACY)
            holder.awarder.text = Html.fromHtml(getItem(position).awarder, Html.FROM_HTML_MODE_LEGACY)
            holder.date.text = Html.fromHtml(getItem(position).date, Html.FROM_HTML_MODE_LEGACY)
        } else {
            holder.title.text = Html.fromHtml(getItem(position).content)
            holder.awarder.text = Html.fromHtml(getItem(position).awarder)
            holder.date.text = Html.fromHtml(getItem(position).date)
        }
    }

    inner class VHItem (itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title: TextView
        val awarder: TextView
        val date: TextView

        init {
            itemView.setOnClickListener(this)
            title = itemView.findViewById(R.id.achievement_title)
            awarder = itemView.findViewById(R.id.achievement_awarder)
            date = itemView.findViewById(R.id.achievement_date)
        }

        override fun onClick(v: View?) {

        }
    }
}
