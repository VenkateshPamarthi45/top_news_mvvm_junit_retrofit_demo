package com.venkatesh.topnewsapp.items.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.venkatesh.networklibrary.model.ModelManager
import com.venkatesh.networklibrarydemo.items.adapter.ItemViewHolder
import com.venkatesh.topnewsapp.R
import com.venkatesh.topnewsapp.items.repository.model.Item
import com.venkatesh.topnewsapp.items.view.ItemListingFragment


class ItemRecyclerViewAdapter(var modelManager: ModelManager?, var mItems: MutableList<Item.Article>, var listener: ItemListingFragment.OnWatchlistItemListener) : RecyclerView.Adapter<ItemViewHolder>() {

    fun addAllItems(items: List<Item.Article>){
        mItems.addAll(items)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemViewHolder =
        ItemViewHolder(
            LayoutInflater.from(p0.context).inflate(
                R.layout.trending_card_view,
                p0,
                false
            ), listener, modelManager
        )

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = mItems.size

    override fun onBindViewHolder(p0: ItemViewHolder, p1: Int){
        p0.setItem(mItems[p1])
    }

}