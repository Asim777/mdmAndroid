package com.mdmbaku.mdmandroid.tabs.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.glide.slider.library.svg.GlideApp
import com.mdmbaku.mdmandroid.ApplicationClass
import com.mdmbaku.mdmandroid.R
import com.mdmbaku.mdmandroid.data.PortfolioItem

class PortfolioAdapter(private val portfolioItems : List<PortfolioItem>) : RecyclerView.Adapter<PortfolioAdapter.VHItem>() {

    private var mItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mItemClickListener = mItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHItem{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.portfolio_item, parent, false)
        return VHItem(v)
    }

    override fun getItemCount(): Int {
        return portfolioItems.size
    }

    fun getItem(position: Int): PortfolioItem {
        return portfolioItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, selectedPortfolioItem: PortfolioItem)
    }

    override fun onBindViewHolder(holder: VHItem, position: Int) {
        holder.title.text = getItem(position).title
        GlideApp.with(ApplicationClass.getAppContext()).load(getItem(position).thumbnail).into(holder.image)
    }

    inner class VHItem (itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val title: TextView
        val image: ImageView

        init {
            itemView.setOnClickListener(this)
            title = itemView.findViewById(R.id.portfolio_title)
            image = itemView.findViewById(R.id.portfolio_image)
        }

        override fun onClick(v: View) {
            if (mItemClickListener != null) {
                mItemClickListener!!.onItemClick(itemView, getItem(adapterPosition))
            }
        }
    }
}