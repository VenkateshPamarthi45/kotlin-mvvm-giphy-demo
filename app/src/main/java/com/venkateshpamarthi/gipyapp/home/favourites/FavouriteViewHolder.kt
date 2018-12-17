package com.venkateshpamarthi.gipyapp.home.favourites

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.venkateshpamarthi.gipyapp.R
import com.venkateshpamarthi.gipyapp.db.FavouriteGiphy

/**
 * Class extends [RecyclerView.ViewHolder]
 * @param itemView is the instance of view
 * @param listener is [FavouriteListingFragment.OnWatchlistItemListener] listener
 */
class FavouriteViewHolder(itemView: View, listener: FavouriteListingFragment.OnWatchlistItemListener): RecyclerView.ViewHolder(itemView) {

    var favouriteGiphy: FavouriteGiphy? = null

    private var imageView: ImageView = itemView.findViewById(R.id.imageView)
    private var favouriteImageView: ImageView = itemView.findViewById(R.id.favouriteImageView)

    init {
        favouriteImageView.setOnClickListener({
            listener.onListClickInteraction(favouriteGiphy!!, it.tag.toString())
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
     * @param giphy is the instance of [FavouriteGiphy]
     */
    fun setFavouriteItem(giphy: FavouriteGiphy) {
        favouriteGiphy = giphy
        if (favouriteGiphy?.is_favourite == 1){
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

        Glide.with(itemView.context).asGif().load(favouriteGiphy?.image_url).into(imageView)
    }

}