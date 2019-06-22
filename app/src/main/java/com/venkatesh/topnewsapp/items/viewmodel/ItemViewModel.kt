package com.venkatesh.topnewsapp.items.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.venkatesh.topnewsapp.common.LiveDataResource
import com.venkatesh.topnewsapp.items.repository.ItemRepository
import com.venkatesh.topnewsapp.items.repository.model.Item

/**
 *
 * @property itemRepository ItemRepository
 * @property items MutableLiveData<MutableList<Item>>?
 * @constructor
 */
class ItemViewModel(private val itemRepository: ItemRepository):ViewModel() {

    private var items : MutableLiveData<LiveDataResource<Item>>? = null
    var savedItems : MutableList<Item.Article>? = null

    /**
     *
     * @param pageId String
     * @return MutableLiveData<MutableList<Item>>?
     */
    fun getItems(pageId: String): MutableLiveData<LiveDataResource<Item>>? {
        items = MutableLiveData()

        itemRepository.getItems(pageId){ liveDataResource ->
            items?.value = liveDataResource
        }
        return items
    }
}