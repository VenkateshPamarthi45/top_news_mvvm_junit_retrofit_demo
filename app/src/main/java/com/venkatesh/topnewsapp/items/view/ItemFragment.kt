package com.venkatesh.topnewsapp.items.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.venkatesh.networklibrary.model.ModelManager
import com.venkatesh.topnewsapp.R
import com.venkatesh.topnewsapp.common.LiveDataResource
import com.venkatesh.topnewsapp.common.isNetworkAvailable
import com.venkatesh.topnewsapp.databinding.ErrorCaseBinding
import com.venkatesh.topnewsapp.databinding.FragmentItemListBinding
import com.venkatesh.topnewsapp.detail.DetailFragment
import com.venkatesh.topnewsapp.items.adapter.ItemRecyclerViewAdapter
import com.venkatesh.topnewsapp.items.repository.model.Item
import com.venkatesh.topnewsapp.items.viewmodel.ItemViewModel
import com.venkatesh.topnewsapp.items.viewmodel.ItemsViewModelFactory
import kotlinx.android.synthetic.main.error_case.*
import kotlinx.android.synthetic.main.fragment_item_list.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.support.kodein
import org.kodein.di.generic.instance
import java.util.*


private lateinit var countingIdlingResource: CountingIdlingResource

class ItemListingFragment : Fragment(),KodeinAware {

    private val TAG = "ItemListingFragment"
    override val kodein by kodein()
    private val modelManager: ModelManager by instance()
    private var columnCount = 1
    private val viewModelFactory : ItemsViewModelFactory by  instance()
    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(ItemViewModel::class.java)
    }

    private var startIndex = 0
    private var isLoading = false
    private var itemRecyclerViewAdapter : ItemRecyclerViewAdapter? = null

    private lateinit var gridLayoutManager: GridLayoutManager
    private var totalCount:Int = 0
    private var items : MutableList<Item.Article>? = null
    private lateinit var listItemBinding: FragmentItemListBinding
    private lateinit var errorCaseBinding: ErrorCaseBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        listItemBinding = FragmentItemListBinding.inflate(layoutInflater, container, false)
        errorCaseBinding = ErrorCaseBinding.inflate(layoutInflater, container, false)
        gridLayoutManager = GridLayoutManager(view.context, resources.getInteger(R.integer.items_columns), LinearLayoutManager.VERTICAL, false)
        listItemBinding.recyclerView.layoutManager = gridLayoutManager
        countingIdlingResource = CountingIdlingResource(TAG)

        setRecyclerViewScrollListener()
        swipeRefreshingLayoutListener()
        return listItemBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("on view created ")
        if(savedInstanceState != null){
            startIndex = savedInstanceState.getInt(getString(R.string.key_start_index))
            val pastItems = savedInstanceState.getInt(getString(R.string.key_recyclerview_position))
            totalCount = savedInstanceState.getInt(getString(R.string.key_total_count))
            items = viewModel.savedItems
            setRecyclerViewAdapterWithData(items!!)
            listItemBinding.recyclerView.scrollToPosition(pastItems)
        }else{
            makePaginationApi()
        }
    }

    override fun onResume() {
        super.onResume()
        println("on resume ")
    }

    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (itemRecyclerViewAdapter != null) {
            outState.putInt(getString(R.string.key_recyclerview_position), gridLayoutManager.findLastVisibleItemPosition())
            outState.putInt(getString(R.string.key_start_index), startIndex)
            outState.putInt(getString(R.string.key_total_count), totalCount)
            viewModel.savedItems = items
        }
    }

    /**
     * In this method [recyclerView] scroll listener is added
     * On scroll changes, calculating totalItems and pastItems from [gridLayoutManager]
     * checking condition it it matches a api call is made
     * and adapter set for updated results by calling
     * @makePaginationApi method
     */
    private fun setRecyclerViewScrollListener(){
        listItemBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItems = recyclerView.layoutManager?.itemCount
                val pastItems = gridLayoutManager.findLastVisibleItemPosition()
                if(pastItems  >= (totalItems!!.minus(2)) && !isLoading && pastItems < totalCount){
                    startIndex = totalItems
                    makePaginationApi()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    /**
     * In this method [swipeRefreshingLayout] refresh listener is set
     * [startIndex] is made zero and [itemRecyclerViewAdapter] is made null
     * such that new api call and adapter set for updated results by calling
     * @makePaginationApi method
     */
    private fun swipeRefreshingLayoutListener(){
        listItemBinding.swipeRefreshingLayout.setOnRefreshListener {
            startIndex = 0
            itemRecyclerViewAdapter = null
            makePaginationApi()
        }
    }

    /**
     * In this method [moreProgressBar] or [progressBar] set to visible
     * @getItemsFromViewModel method or
     */
    fun makePaginationApi(){
        if(startIndex != 0){
            moreProgressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.VISIBLE
        }
        isLoading = true
        getItemsFromViewModel()
    }

    /**
     * In this method view are handled to display no network state
     * [errorImageView] shows no wifi drawable
     * [errorButton] shows retry option
     */
    private fun handlingNoNetworkCase() {
        if (startIndex == 0) {
            errorCaseBinding.errorCaseButton.visibility = View.VISIBLE
            errorCaseBinding.errorCaseButton.text = (getString(R.string.retry))
            errorCaseBinding.errorMessageTextView.text = getString(R.string.no_internet_connection)
            errorCaseBinding.errorMessageTextView.visibility = View.VISIBLE
            errorImageView.setBackgroundResource(R.drawable.ic_signal_wifi_off)
            errorImageView.visibility = View.VISIBLE
            errorCaseBinding.errorCaseButton.setOnClickListener {
                makePaginationApi()
            }
        }else{
            Snackbar.make(view!!,getString(R.string.no_internet_connection),Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * In this method view are handled to dismiss error views
     * [errorImageView] visibility gone
     * [errorButton] visibility gone
     * [errorTextView] visibility gone
     */
    private fun dismissErrorViews() {
        errorCaseBinding.errorCaseButton.visibility = View.GONE
        errorCaseBinding.errorMessageTextView.visibility = View.GONE
        errorImageView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    /**
     * In this method items are fetched from [viewModel]
     * and data is set to views calling
     * @setRecyclerViewAdapterWithData method
     */
    private fun getItemsFromViewModel() {
        if (context != null && context!!.isNetworkAvailable()) {
            dismissErrorViews()
            listItemBinding.progressBar.visibility = View.VISIBLE
            countingIdlingResource.increment()
            val countryCode = Locale.getDefault().country
            viewModel.getItems("v2/top-headlines?country=$countryCode&apiKey=ca2e79a2b7694f1895c53bfc3cc9c4c6")?.observe(this, Observer {
                dismissProgressBarViews()
                if (!countingIdlingResource.isIdleNow) {
                    countingIdlingResource.decrement()
                }
                if (it?.status == LiveDataResource.Status.SUCCESS && it.data != null && it.data.articles.isNotEmpty()) {
                    totalCount = it.data.totalResults
                    val listOfItems = it.data.articles.toMutableList()
                    setRecyclerViewAdapterWithData(listOfItems)
                }
            })
        } else {
            dismissProgressBarViews()
            handlingNoNetworkCase()
        }
    }

    /**
     * In this method [recyclerView] is set based on if
     * [itemRecyclerViewAdapter] is null getting favourite items from [viewModel]
     * or adding items to adapter and notify it.
     */
    private fun setRecyclerViewAdapterWithData(listOfItems: MutableList<Item.Article>) {
        if (itemRecyclerViewAdapter == null && recyclerView.adapter == null) {
            items = listOfItems
            itemRecyclerViewAdapter = ItemRecyclerViewAdapter(
                modelManager, listOfItems, object :
                    OnWatchlistItemListener {
                    override fun onListClickInteraction(item: Any, tag: String) {
                        val article = item as Item.Article
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container, DetailFragment.newInstance(article.url), DetailFragment::class.java.name.toString())?.addToBackStack(DetailFragment::class.java.name.toString())?.commit()
                    }
                })
            recyclerView.adapter = itemRecyclerViewAdapter
        } else {
            itemRecyclerViewAdapter?.addAllItems(listOfItems)
            items?.addAll(listOfItems)
            recyclerView.post {
                itemRecyclerViewAdapter?.notifyDataSetChanged()
            }

        }
    }

    /**
     * In this method disabling all progress views
     * [progressBar] [moreProgressBar] visibility is made gone
     * [swipeRefreshingLayout] is refreshing made false
     */
    private fun dismissProgressBarViews() {
        isLoading = false
        progressBar.visibility = View.GONE
        moreProgressBar.visibility = View.GONE
        listItemBinding.swipeRefreshingLayout.isRefreshing = false
    }

    /**
     * This interface is used to communicate with click listener in [itemRecyclerViewAdapter]
     * [ItemViewHolder] view click listeners are mapped to this method
     */
    interface OnWatchlistItemListener {
        fun onListClickInteraction(item: Any, tag: String)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ItemListingFragment().apply {}

        fun getCountingIdlingResource(): CountingIdlingResource =
            countingIdlingResource
    }
}