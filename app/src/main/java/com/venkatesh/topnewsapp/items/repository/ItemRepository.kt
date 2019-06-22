
package com.venkatesh.topnewsapp.items.repository
import com.venkatesh.topnewsapp.common.LiveDataResource
import com.venkatesh.topnewsapp.items.repository.model.Item

interface ItemRepository {
    fun getItems(pageId: String, closure: (liveDataResource: LiveDataResource<Item>) -> Unit)
}