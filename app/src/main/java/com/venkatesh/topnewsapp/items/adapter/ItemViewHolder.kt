package com.venkatesh.networklibrarydemo.items.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.venkatesh.networklibrary.createBitMapFromResponseBody
import com.venkatesh.networklibrary.model.ModelManager
import com.venkatesh.topnewsapp.R
import com.venkatesh.topnewsapp.items.repository.model.Item
import com.venkatesh.topnewsapp.items.view.ItemListingFragment

/**
 * Class extends [RecyclerView.ViewHolder]
 * @param itemView is the instance of view
 * @param listener is [ItemListingFragment.OnWatchlistItemListener] listener
 */
class ItemViewHolder(itemView: View, listener: ItemListingFragment.OnWatchlistItemListener, var modelManager: ModelManager?): RecyclerView.ViewHolder(itemView) {

    private var item: Item.Article? = null
    private var imageView: ImageView = itemView.findViewById(R.id.imageView)
    init {
        imageView.setOnClickListener {
            listener.onListClickInteraction(
                item!!, ""
            )
        }
    }

    /**
     * In this method, the views are updated with data
     * @param item is the instance of [Item]
     */
    fun setItem(item: Item.Article ) {
        this.item = item
        modelManager?.getRequestForImage(item.urlToImage){
            response, responseBody, call ->
            if(responseBody != null){
                imageView.createBitMapFromResponseBody(responseBody)
            }
        }
    }

}