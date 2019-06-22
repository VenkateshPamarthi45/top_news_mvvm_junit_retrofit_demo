package com.venkatesh.topnewsapp.viewmodel

import com.venkatesh.topnewsapp.items.viewmodel.ItemViewModel

import org.junit.Assert
import org.junit.Test

class ItemViewModelTest {
    private val mockItemRepository = MockItemRepository()
    private var sut = ItemViewModel(mockItemRepository)

    @Test
    fun testCheckItemRepositoryGetMethodIsCalledWhenGetItemsInViewModelIsCalled(){
        val items = sut.getItems("sampleUrl")
        Assert.assertTrue(mockItemRepository.isGetItemsMethodIsCalled)
    }
}