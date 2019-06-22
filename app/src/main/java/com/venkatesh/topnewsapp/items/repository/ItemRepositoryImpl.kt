
package com.venkatesh.topnewsapp.items.repository

import com.google.gson.Gson
import com.venkatesh.networklibrary.model.ModelManager
import com.venkatesh.topnewsapp.common.LiveDataResource
import com.venkatesh.topnewsapp.items.repository.model.Item

/**
 *
 * @property modelManager ModelManager
 * @constructor
 */
class ItemRepositoryImpl(var modelManager: ModelManager): ItemRepository {

    /**
     *
     * @param pageId String
     * @param closure Function4<[@kotlin.ParameterName] Response<ResponseBody>?, [@kotlin.ParameterName] List<Item>?, [@kotlin.ParameterName] String?, [@kotlin.ParameterName] Deferred<Response<ResponseBody>>?, Unit>
     */
    override fun getItems(pageId: String, closure: (liveDataSource: LiveDataResource<Item>) -> Unit) {


        modelManager.getRequest(pageId){ response, responseBodyString,  call ->
            if(responseBodyString == null && response == null && call != null){
                println(" call is not null")
                closure(LiveDataResource.loading(null, call))
            } else if(responseBodyString != null){
                println("responseBodyString is not null " + responseBodyString)
                var item  = Gson().fromJson(responseBodyString, Item::class.java)
                val statusCode = response?.code() ?: 0
                closure(LiveDataResource.success(statusCode,item, call))
            }else if(response != null){
                println("responseBodyString is null")
                closure(LiveDataResource.error(response.code(), response.message(), null, call))
            }else{
                println(" response is null")
                closure(LiveDataResource.error(500, "Internal Error", null, call))
            }
        }
    }
}