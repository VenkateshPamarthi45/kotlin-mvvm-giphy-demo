package com.venkateshpamarthi.gipyapp.home.common

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.venkateshpamarthi.gipyapp.db.FavouriteGiphy
import com.venkateshpamarthi.gipyapp.db.FavouriteRoom
import com.venkateshpamarthi.gipyapp.network.GiphyObject
import com.venkateshpamarthi.gipyapp.network.LiveDataResource
import com.venkateshpamarthi.gipyapp.network.ResponseModel

class GiphyViewModel: ViewModel(), GiphyViewModelContract {

    private var trendingGiphys : MutableLiveData<LiveDataResource<ResponseModel>>? = null
    private var searchGiphys : MutableLiveData<LiveDataResource<ResponseModel>>? = null
    private var favouriteIds : MutableLiveData<List<String>>? = null
    private var favouriteGiphys : MutableLiveData<LiveDataResource<List<FavouriteGiphy>>>? = null
    var savedTrendingGiphys : MutableList<GiphyObject>? = null
    var savedFavouriteGiphys : MutableList<FavouriteGiphy>? = null

    /**
     * In this method trending giphys were fetched from [giphyRepo]
     * @param limit is the limit for number of items
     * @param offSet is the offset value
     * @param giphyRepo is the instance of [GiphyRepo]
     * @return trendingGiphys this is the instance of MutableLiveData<LiveDataResource<ResponseModel>>
     */
    override fun getTrendingGiphys(limit : Int, offSet: Int, giphyRepo: GiphyRepo): MutableLiveData<LiveDataResource<ResponseModel>>? {
        trendingGiphys = MutableLiveData()
        giphyRepo.getTrendingGiphys(limit, offSet, trendingGiphys!!)
        return trendingGiphys
    }

    /**
     * In this method search giphys were fetched from [giphyRepo]
     * @param limit is the limit for number of items
     * @param offSet is the offset value
     * @param giphyRepo is the instance of [GiphyRepo]
     * @return searchGiphys this is the instance of MutableLiveData<LiveDataResource<ResponseModel>>
     */
    override fun getSearchGiphys(query:String, limit : Int, offSet: Int, giphyRepo: GiphyRepo): MutableLiveData<LiveDataResource<ResponseModel>>? {
        searchGiphys = MutableLiveData()
        giphyRepo.getSearchGiphys(query, limit, offSet, searchGiphys!!)
        return searchGiphys
    }

    /**
     * In this method we are fetching favourite giphys from [giphyRepo]
     * @param limit is the limit for number of items
     * @param offSet is the offset value
     * @param room is the instance of [FavouriteRoom]
     * @param giphyRepo is the instance of [GiphyRepo]
     * @return favouriteGiphys this is the instance of MutableLiveData<LiveDataResource<List<FavouriteGiphy>>>
     */
    override fun getFavouriteGiphys(limit:Int, offset:Int,room: FavouriteRoom, giphyRepo: GiphyRepo): MutableLiveData<LiveDataResource<List<FavouriteGiphy>>>? {
        favouriteGiphys = MutableLiveData()
        giphyRepo.getFavouriteGiphys(limit, offset, room, favouriteGiphys!!)
        return favouriteGiphys
    }

    /**
     * In this method, performing add or remove operation of favourite giphys in [giphyRepo]
     * @param item is the limit for number of items
     * @param tag is the tag
     * @param favouriteRoom is the instance of [FavouriteRoom]
     * @param giphyRepo is the instance of [GiphyRepo]
     */
    override fun addOrRemoveFavouriteItemInDb(item:Any, tag: String,favouriteRoom: FavouriteRoom, giphyRepo: GiphyRepo){
        giphyRepo.addOrRemoveFavouriteItemFromDb(item,tag,favouriteRoom)
    }

    /**
     * In this method, get favourite ids from [giphyRepo]
     * @param favouriteRoom is the instance of [FavouriteRoom]
     * @param giphyRepo is the instance of [GiphyRepo]
     *
     * @return favouriteIds this is the instance of MutableLiveData<LiveDataResource<List<String>>>
     */
    override fun getFavouriteIdList(favouriteRoom: FavouriteRoom, giphyRepo: GiphyRepo):MutableLiveData<List<String>>?{
        favouriteIds = MutableLiveData()
        giphyRepo.getFavouriteIdList(favouriteRoom, favouriteIds!!)
        return favouriteIds
    }
}

/**
 * This interface helps to maintain contract of [GiphyViewModel]
 */
interface GiphyViewModelContract {

    fun getTrendingGiphys(limit : Int, offSet: Int, giphyRepo: GiphyRepo): MutableLiveData<LiveDataResource<ResponseModel>>?
    fun getSearchGiphys(query:String, limit : Int, offSet: Int, giphyRepo: GiphyRepo): MutableLiveData<LiveDataResource<ResponseModel>>?
    fun getFavouriteGiphys(limit:Int, offset:Int,room: FavouriteRoom, giphyRepo: GiphyRepo): MutableLiveData<LiveDataResource<List<FavouriteGiphy>>>?
    fun addOrRemoveFavouriteItemInDb(item:Any, tag: String,favouriteRoom: FavouriteRoom, giphyRepo: GiphyRepo)
    fun getFavouriteIdList(favouriteRoom: FavouriteRoom, giphyRepo: GiphyRepo):MutableLiveData<List<String>>?
}