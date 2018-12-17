package com.venkateshpamarthi.gipyapp.home.common

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.venkateshpamarthi.gipyapp.*
import com.venkateshpamarthi.gipyapp.db.FavouriteGiphy
import com.venkateshpamarthi.gipyapp.home.favourites.FavouriteListingFragment
import com.venkateshpamarthi.gipyapp.home.favourites.FavouriteViewHolder
import com.venkateshpamarthi.gipyapp.network.GiphyObject
import com.venkateshpamarthi.gipyapp.home.trending.TrendingViewHolder
import com.venkateshpamarthi.gipyapp.home.trending.TrendingListingFragment





class TrendingGiphysRecyclerViewAdapter(var favouriteIdList: List<String>?,var mGiphys: MutableList<GiphyObject>, var listener: TrendingListingFragment.OnWatchlistItemListener) : RecyclerView.Adapter<TrendingViewHolder>() {
    var lastPosition = -1

    fun addAllItems(giphs: List<GiphyObject>){
        mGiphys.addAll(giphs)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TrendingViewHolder =
        TrendingViewHolder(
            LayoutInflater.from(p0.context).inflate(
                R.layout.trending_card_view,
                p0,
                false
            ), listener
        )

    override fun getItemCount(): Int = mGiphys.size

    override fun onViewDetachedFromWindow(holder: TrendingViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    override fun onBindViewHolder(p0: TrendingViewHolder, p1: Int){
        val animation = AnimationUtils.loadAnimation(p0.itemView.context,
            if (p1 > lastPosition)
                R.anim.up_from_bottom
            else
                R.anim.down_from_top
        )
        p0.itemView.startAnimation(animation)
        lastPosition = p1
        p0.setItem(mGiphys[p1],favouriteIdList)
    }

}


class FavouriteRecyclerViewAdapter(var mGiphys: MutableList<FavouriteGiphy>, var listener: FavouriteListingFragment.OnWatchlistItemListener) : RecyclerView.Adapter<FavouriteViewHolder>() {

    fun addItems(newItems: List<FavouriteGiphy>) = mGiphys.addAll(newItems)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FavouriteViewHolder =
        FavouriteViewHolder(
            LayoutInflater.from(p0.context).inflate(
                R.layout.trending_card_view,
                p0,
                false
            ), listener
        )

    override fun getItemCount(): Int = mGiphys.size

    override fun onBindViewHolder(p0: FavouriteViewHolder, p1: Int) = p0.setFavouriteItem(mGiphys[p1])

}