package com.venkatesh.topnewsapp.viewmodel


import com.venkatesh.topnewsapp.common.LiveDataResource
import com.venkatesh.topnewsapp.items.repository.ItemRepository
import com.venkatesh.topnewsapp.items.repository.model.Item

class MockItemRepository: ItemRepository {

    var isGetItemsMethodIsCalled = false
    override fun getItems(pageId: String, closure: (liveDataResource: LiveDataResource<Item>) -> Unit) {
        isGetItemsMethodIsCalled = true
        if(pageId == "sampleUrl1"){
            closure(LiveDataResource.error(404, "Invalid user", null, null))
        }
    }

}
