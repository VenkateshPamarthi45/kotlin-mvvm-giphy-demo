package com.venkateshpamarthi.gipyapp.home.favourites

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.venkateshpamarthi.gipyapp.R
import com.venkateshpamarthi.gipyapp.R.id.errorImageView
import com.venkateshpamarthi.gipyapp.home.common.FavouriteRecyclerViewAdapter
import com.venkateshpamarthi.gipyapp.home.common.GiphyRepo
import com.venkateshpamarthi.gipyapp.home.common.GiphyViewModel
import com.venkateshpamarthi.gipyapp.db.FavouriteGiphy
import com.venkateshpamarthi.gipyapp.db.FavouriteRoom
import com.venkateshpamarthi.gipyapp.network.LiveDataResource

class FavouriteListingFragment : Fragment() {

    private lateinit var viewModel: GiphyViewModel
    private var startIndex = 0
    private var isLoading = false
    private lateinit var moreProgressBar:ProgressBar
    private lateinit var progressBar:ProgressBar
    private var favouriteRecyclerViewAdapter : FavouriteRecyclerViewAdapter? = null
    private lateinit var recyclerView : RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var swipeRefreshingLayout: SwipeRefreshLayout
    private lateinit var errorImageView: AppCompatImageView
    private lateinit var errorTextView: AppCompatTextView
    private lateinit var errorButton: AppCompatButton
    private var favouriteGiphys : MutableList<FavouriteGiphy>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.listing_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        moreProgressBar = view.findViewById(R.id.moreProgressBar)
        progressBar = view.findViewById(R.id.progressBar)
        swipeRefreshingLayout = view.findViewById(R.id.swipe_refreshing_layout)

        errorImageView = view.findViewById(R.id.errorImageView)
        errorTextView = view.findViewById(R.id.errorMessageTextView)
        errorButton = view.findViewById(R.id.errorCaseButton)

        gridLayoutManager = GridLayoutManager(view.context, resources.getInteger(R.integer.favourite_giphys_columns), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        setRecyclerViewScrollListener()
        swipeRefreshingLayoutListener()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GiphyViewModel::class.java)
        if(savedInstanceState != null){
            startIndex = savedInstanceState.getInt(getString(R.string.key_start_index))
            val pastItems = savedInstanceState.getInt(getString(R.string.key_recyclerview_position))
            favouriteGiphys = viewModel.savedFavouriteGiphys
            setRecyclerViewAdapterWithData(favouriteGiphys!!)
            recyclerView.scrollToPosition(pastItems)
        }else{
            makePaginationApi()
        }
    }
    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (favouriteRecyclerViewAdapter != null) {
            outState.putInt(getString(R.string.key_recyclerview_position), gridLayoutManager.findLastVisibleItemPosition())
            outState.putInt(getString(R.string.key_start_index), startIndex)
            viewModel.savedFavouriteGiphys = favouriteGiphys
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
                if(pastItems  >= (totalItems!!.minus(2)) && !isLoading){
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
     * [startIndex] is made zero and [favouriteRecyclerViewAdapter] is made null
     * such that new api call and adapter set for updated results by calling
     * @makePaginationApi method
     */
    private fun swipeRefreshingLayoutListener(){
        swipeRefreshingLayout.setOnRefreshListener {
            startIndex = 0
            favouriteRecyclerViewAdapter = null
            makePaginationApi()
        }
    }

    /**
     * In this method [moreProgressBar] or [progressBar] set to visible
     * get favourite items from view model and setting recyclerview adapter
     */
    fun makePaginationApi(){
        if(startIndex != 0){
            moreProgressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.VISIBLE
        }
        val instance = FavouriteRoom.getInstance(context!!)
        viewModel.getFavouriteGiphys(20, startIndex, instance!!, GiphyRepo())?.observe(this, Observer {
            dismissProgressBarViews()
            if(it?.status == LiveDataResource.Status.SUCCESS){
                dismissErrorViews()
               setRecyclerViewAdapterWithData(it.data!!)
            }else{
                handlingNoFavouritesCase()
            }
        })
    }


    /**
     * In this method [recyclerView] is set based on if
     * [favouriteRecyclerViewAdapter] is null getting favourite items from [viewModel]
     * or adding items to adapter and notify it.
     */
    private fun setRecyclerViewAdapterWithData(data: List<FavouriteGiphy>) {
        if(favouriteRecyclerViewAdapter == null) {
            favouriteGiphys = data.toMutableList()
            favouriteRecyclerViewAdapter = FavouriteRecyclerViewAdapter(
                data.toMutableList(), object :
                    OnWatchlistItemListener {
                    override fun onListClickInteraction(item: Any, tag: String) {
                        viewModel.addOrRemoveFavouriteItemInDb(
                            item,
                            tag,
                            FavouriteRoom.getInstance(context!!)!!,
                            GiphyRepo()
                        )
                    }
                })
            recyclerView.adapter = favouriteRecyclerViewAdapter
        }else{
            favouriteGiphys?.addAll(data)
            favouriteRecyclerViewAdapter?.addItems(data)
            favouriteRecyclerViewAdapter?.notifyDataSetChanged()
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
     * In this method view are handled to display no favourite state
     * [errorImageView] shows no wifi drawable
     * [errorButton] shows retry option
     */
    private fun handlingNoFavouritesCase() {
        if (startIndex == 0) {
            errorButton.visibility = View.VISIBLE
            errorButton.text = (getString(R.string.retry))
            errorTextView.text = getString(R.string.no_results_found)
            errorTextView.visibility = View.VISIBLE
            errorImageView.setBackgroundResource(R.drawable.ic_not_found)
            errorImageView.visibility = View.VISIBLE
            errorButton.setOnClickListener {
                makePaginationApi()
            }
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
     * This interface is used to communicate with click listener in [FavouriteRecyclerViewAdapter]
     * [FavouriteViewHolder] view click listeners are mapped to this method
     */
    interface OnWatchlistItemListener {
        fun onListClickInteraction(item: Any, tag: String)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavouriteListingFragment().apply {}
    }

}
