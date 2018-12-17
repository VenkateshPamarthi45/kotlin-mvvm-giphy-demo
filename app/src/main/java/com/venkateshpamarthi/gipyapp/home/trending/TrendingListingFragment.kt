package com.venkateshpamarthi.gipyapp.home.trending

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.venkateshpamarthi.gipyapp.R
import com.venkateshpamarthi.gipyapp.home.common.TrendingGiphysRecyclerViewAdapter
import com.venkateshpamarthi.gipyapp.home.common.GiphyRepo
import com.venkateshpamarthi.gipyapp.home.common.GiphyViewModel
import com.venkateshpamarthi.gipyapp.db.FavouriteRoom
import android.net.ConnectivityManager
import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.v7.widget.*
import com.venkateshpamarthi.gipyapp.R.id.*
import com.venkateshpamarthi.gipyapp.network.GiphyObject


private lateinit var countingIdlingResource: CountingIdlingResource

class TrendingListingFragment : Fragment() {

    private val TAG = "TrendingListingFragment"

    private lateinit var viewModel: GiphyViewModel
    private var startIndex = 0
    private var isLoading = false
    private var isSearchEnabled = false
    private var searchQuery:String? = null
    private lateinit var moreProgressBar:ProgressBar
    private lateinit var progressBar:ProgressBar
    private var trendingGiphysRecyclerViewAdapter : TrendingGiphysRecyclerViewAdapter? = null
    private lateinit var recyclerView : RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var swipeRefreshingLayout: SwipeRefreshLayout
    private lateinit var errorImageView: AppCompatImageView
    private lateinit var errorTextView: AppCompatTextView
    private lateinit var errorButton: AppCompatButton
    private var totalCount:Int = 0
    private var trendingGiphys : MutableList<GiphyObject>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.listing_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(GiphyViewModel::class.java)
        recyclerView = view.findViewById(R.id.recyclerView)
        moreProgressBar = view.findViewById(R.id.moreProgressBar)
        progressBar = view.findViewById(R.id.progressBar)

        errorImageView = view.findViewById(R.id.errorImageView)
        errorTextView = view.findViewById(R.id.errorMessageTextView)
        errorButton = view.findViewById(R.id.errorCaseButton)

        swipeRefreshingLayout = view.findViewById(R.id.swipe_refreshing_layout)
        gridLayoutManager = GridLayoutManager(view.context, resources.getInteger(R.integer.trending_giphys_columns), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        countingIdlingResource = CountingIdlingResource(TAG)

        setRecyclerViewScrollListener()
        swipeRefreshingLayoutListener()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState != null){
            startIndex = savedInstanceState.getInt(getString(R.string.key_start_index))
            val pastItems = savedInstanceState.getInt(getString(R.string.key_recyclerview_position))
            totalCount = savedInstanceState.getInt(getString(R.string.key_total_count))
            trendingGiphys = viewModel.savedTrendingGiphys
            setRecyclerViewAdapterWithData(trendingGiphys!!)
            recyclerView.scrollToPosition(pastItems)
        }else{
            makePaginationApi()
        }
    }

    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (trendingGiphysRecyclerViewAdapter != null) {
            outState.putInt(getString(R.string.key_recyclerview_position), gridLayoutManager.findLastVisibleItemPosition())
            outState.putInt(getString(R.string.key_start_index), startIndex)
            outState.putInt(getString(R.string.key_total_count), totalCount)
            viewModel.savedTrendingGiphys = trendingGiphys
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
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
     * In this method helps to detect network state
     * connecting with [ConnectivityManager] and getting network info
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /**
     * In this method [swipeRefreshingLayout] refresh listener is set
     * [startIndex] is made zero and [trendingGiphysRecyclerViewAdapter] is made null
     * such that new api call and adapter set for updated results by calling
     * @makePaginationApi method
     */
    private fun swipeRefreshingLayoutListener(){
        swipeRefreshingLayout.setOnRefreshListener {
            startIndex = 0
            trendingGiphysRecyclerViewAdapter = null
            makePaginationApi()
        }
    }

    /**
     * In this method [MainActivity] passed query from [android.support.v7.widget.SearchView]
     * [startIndex] is made zero and [trendingGiphysRecyclerViewAdapter] is made null
     * such that search api call and adapter set for updated results by calling
     * @makePaginationApi method
     *
     * @param query string to search for giphys
     */
    fun onSearchQuery(query:String){
        startIndex = 0
        searchQuery = query
        trendingGiphysRecyclerViewAdapter = null
        isSearchEnabled = true
        makePaginationApi()
    }

    /**
     * In this method [moreProgressBar] or [progressBar] set to visible
     * based on [isSearchEnabled] value calling
     * @getTrendingGiphysFromViewModel method or
     * @getSearchResultsForGiphysFromViewModel method
     */
    fun makePaginationApi(){
        if(startIndex != 0){
            moreProgressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.VISIBLE
        }
        if (!isSearchEnabled) {
            isLoading = true
            getTrendingGiphysFromViewModel()
        }else if(isSearchEnabled){
            isLoading = true
            getSearchResultsForGiphysFromViewModel()
        }
    }

    /**
     * In this method search results are fetched from [viewModel]
     * and data is set to views calling
     * @setRecyclerViewAdapterWithData method
     */
    private fun getSearchResultsForGiphysFromViewModel() {
        if (isNetworkAvailable()) {
            dismissErrorViews()
            countingIdlingResource.increment()
            viewModel.getSearchGiphys(searchQuery!!, 20, startIndex, GiphyRepo())?.observe(this, Observer {
                dismissProgressBarViews()
                if (!countingIdlingResource.isIdleNow()) {
                    countingIdlingResource.decrement()
                }
                if (it?.data?.meta?.status == 200 && it.data.pagination.count > 0) {
                    totalCount = it.data.pagination.total_count
                    val listOfItems = it.data.data.toMutableList()
                    setRecyclerViewAdapterWithData(listOfItems)
                }else{
                    handlingNoSearchResultsFound()
                }
            })
        } else {
            dismissProgressBarViews()
            handlingNoNetworkCase()
        }
    }

    /**
     * In this method view are handled to display no network state
     * [errorImageView] shows not found  drawable
     * [errorButton] visibility gone
     */
    private fun handlingNoSearchResultsFound() {
        if (startIndex == 0) {
            errorButton.visibility = View.GONE
            errorTextView.text = getString(R.string.no_results_found)
            errorTextView.visibility = View.VISIBLE
            errorImageView.setBackgroundResource(R.drawable.ic_not_found)
            errorImageView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    /**
     * In this method view are handled to display no network state
     * [errorImageView] shows no wifi drawable
     * [errorButton] shows retry option
     */
    private fun handlingNoNetworkCase() {
        if (startIndex == 0) {
            errorButton.visibility = View.VISIBLE
            errorButton.text = (getString(R.string.retry))
            errorTextView.text = getString(R.string.no_internet_connection)
            errorTextView.visibility = View.VISIBLE
            errorImageView.setBackgroundResource(R.drawable.ic_signal_wifi_off)
            errorImageView.visibility = View.VISIBLE
            errorButton.setOnClickListener {
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
        errorButton.visibility = View.GONE
        errorTextView.visibility = View.GONE
        errorImageView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    /**
     * In this method trending giphys are fetched from [viewModel]
     * and data is set to views calling
     * @setRecyclerViewAdapterWithData method
     */
    private fun getTrendingGiphysFromViewModel() {
        if (isNetworkAvailable()) {
            dismissErrorViews()
            countingIdlingResource.increment()
            viewModel.getTrendingGiphys(20, startIndex, GiphyRepo())?.observe(this, Observer {
                dismissProgressBarViews()
                if (!countingIdlingResource.isIdleNow()) {
                    countingIdlingResource.decrement()
                }
                if (it?.data?.meta?.status == 200) {
                    totalCount = it.data.pagination.total_count
                    val listOfItems = it.data.data.toMutableList()
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
     * [trendingGiphysRecyclerViewAdapter] is null getting favourite items from [viewModel]
     * or adding items to adapter and notify it.
     */
    private fun setRecyclerViewAdapterWithData(listOfItems: MutableList<GiphyObject>) {
        if (trendingGiphysRecyclerViewAdapter == null) {
            trendingGiphys = listOfItems
            viewModel.getFavouriteIdList(FavouriteRoom.getInstance(context!!)!!,
                GiphyRepo()
            )?.observe(this, Observer {
                trendingGiphysRecyclerViewAdapter = TrendingGiphysRecyclerViewAdapter(
                    it, listOfItems, object : TrendingListingFragment.OnWatchlistItemListener {
                        override fun onListClickInteraction(item: Any, tag: String) {
                            viewModel.addOrRemoveFavouriteItemInDb(item, tag, FavouriteRoom.getInstance(context!!)!!, GiphyRepo())
                        }
                    })
                recyclerView.adapter = trendingGiphysRecyclerViewAdapter
            })
        } else {
            trendingGiphysRecyclerViewAdapter?.addAllItems(listOfItems)
            trendingGiphys?.addAll(listOfItems)
            trendingGiphysRecyclerViewAdapter?.notifyDataSetChanged()
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
        swipeRefreshingLayout.isRefreshing = false
    }

    /**
     * In this method [MainActivity] notify [TrendingListingFragment] that [android.support.v7.widget.SearchView]
     * is closed and [startIndex] is made zero and [trendingGiphysRecyclerViewAdapter] is made null
     * [isSearchEnabled] made false
     * such that trending giphys api call and adapter set for updated results by calling
     * @makePaginationApi method
     */
    fun onSearchCanceled() {
        startIndex = 0
        trendingGiphysRecyclerViewAdapter = null
        isSearchEnabled = false
        makePaginationApi()
    }

    /**
     * This interface is used to communicate with click listener in [trendingGiphysRecyclerViewAdapter]
     * [TrendingViewHolder] view click listeners are mapped to this method
     */
    interface OnWatchlistItemListener {
        fun onListClickInteraction(item: Any, tag: String)
    }

    companion object {
        @JvmStatic
        fun newInstance() = TrendingListingFragment().apply {}

        fun getCountingIdlingResource(): CountingIdlingResource = countingIdlingResource
    }
}
