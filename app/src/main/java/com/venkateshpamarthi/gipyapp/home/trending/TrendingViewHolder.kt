package com.venkateshpamarthi.gipyapp.home.trending

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.venkateshpamarthi.gipyapp.R
import com.venkateshpamarthi.gipyapp.network.GiphyObject

/**
 * Class extends [RecyclerView.ViewHolder]
 * @param itemView is the instance of view
 * @param listener is [TrendingListingFragment.OnWatchlistItemListener] listener
 */
public class TrendingViewHolder(itemView: View, listener: TrendingListingFragment.OnWatchlistItemListener): RecyclerView.ViewHolder(itemView) {

    private var giphyObject: GiphyObject? = null
    private var imageView: ImageView = itemView.findViewById(R.id.imageView)
    private var favouriteImageView: ImageView = itemView.findViewById(R.id.favouriteImageView)

    init {
        favouriteImageView.setOnClickListener({
            listener.onListClickInteraction(giphyObject!!, it.tag.toString())
            if(it.tag == favouriteImageView.context.getString(R.string.favourite_view_tag_unselected)){
                favouriteImageView.setImageDrawable(ContextCompat.getDrawable(favouriteImageView.context,
                    R.drawable.ic_favorite_selected
                ))
                favouriteImageView.tag = favouriteImageView.context.getString(R.string.favourite_view_tag_selected)
            }else{
                favouriteImageView.setImageDrawable(ContextCompat.getDrawable(favouriteImageView.context,
                    R.drawable.ic_favorite_black
                ))
                favouriteImageView.tag = favouriteImageView.context.getString(R.string.favourite_view_tag_unselected)
            }
        })
    }

    /**
     * In this method, the views are updated with data
     * @param giphy is the instance of [GiphyObject]
     * @param favouriteIdList are list of favourite item ids
     */
    fun setItem(giphy: GiphyObject, favouriteIdList: List<String>?) {
        giphyObject = giphy
        if (favouriteIdList != null && favouriteIdList.contains(giphyObject?.id)){
            favouriteImageView.setImageDrawable(ContextCompat.getDrawable(favouriteImageView.context,
                R.drawable.ic_favorite_selected
            ))
            favouriteImageView.tag = favouriteImageView.context.getString(R.string.favourite_view_tag_selected)
        }else{
            favouriteImageView.setImageDrawable(ContextCompat.getDrawable(favouriteImageView.context,
                R.drawable.ic_favorite_black
            ))
            favouriteImageView.tag = favouriteImageView.context.getString(R.string.favourite_view_tag_unselected)
        }
        Glide.with(itemView.context).asGif().load(giphyObject?.images?.original?.url).into(imageView)
    }

}