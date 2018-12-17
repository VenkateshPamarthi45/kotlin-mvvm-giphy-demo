package com.venkateshpamarthi.gipyapp.home.common

import android.arch.lifecycle.MutableLiveData
import com.venkateshpamarthi.gipyapp.R
import com.venkateshpamarthi.gipyapp.db.FavouriteGiphy
import com.venkateshpamarthi.gipyapp.db.FavouriteRoom
import com.venkateshpamarthi.gipyapp.network.GiphyNetworkManager
import com.venkateshpamarthi.gipyapp.network.GiphyObject
import com.venkateshpamarthi.gipyapp.network.LiveDataResource
import com.venkateshpamarthi.gipyapp.network.ResponseModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Communicates with [GiphyViewModel] via [MutableLiveData]
 * It handles mainly with network layer and local database
 */
class GiphyRepo : GiphyRepoContract{


    /**
     * In this method trending giphys were fetched from [GiphyNetworkManager]
     * @param limit is the limit for number of items
     * @param offSet is the offset value
     * @param trendingGiphys this is the instance of MutableLiveData<LiveDataResource<ResponseModel>>
     */
    override fun getTrendingGiphys(limit: Int, offSet: Int, trendingGiphys: MutableLiveData<LiveDataResource<ResponseModel>>) {
        val networkManager = GiphyNetworkManager.create()
        networkManager.getTrending("",limit,offSet)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe ({
                    result ->
                trendingGiphys.value = LiveDataResource(LiveDataResource.Status.SUCCESS, result.meta.status, result, null)
            }, { error ->
                error.printStackTrace()
                trendingGiphys.value = LiveDataResource(LiveDataResource.Status.ERROR, 500, null, error.message)
            })
    }

    /**
     * In this method trending giphys were fetched from [GiphyNetworkManager]
     * @param limit is the limit for number of items
     * @param offSet is the offset value
     * @param searchGiphys this is the instance of MutableLiveData<LiveDataResource<ResponseModel>>
     */
    override fun getSearchGiphys(query:String, limit: Int, offSet: Int, searchGiphys: MutableLiveData<LiveDataResource<ResponseModel>>) {
        val networkManager = GiphyNetworkManager.create()
        networkManager.getSearch("",query,limit,offSet)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe ({
                    result ->
                searchGiphys.value = LiveDataResource(LiveDataResource.Status.SUCCESS, result.meta.status, result, null)
            }, { error ->
                error.printStackTrace()
                searchGiphys.value = LiveDataResource(LiveDataResource.Status.ERROR, 500, null, error.message)
            })
    }

    /**
     * In this method favourite giphys were fetched from [FavouriteRoom]
     * @param limit is the limit for number of items
     * @param offset is the offset value
     * @param room is the instance of [FavouriteRoom]
     * @param favGiphysLiveData this is the instance of MutableLiveData<LiveDataResource<List<FavouriteGiphy>>>
     */
    override fun getFavouriteGiphys(limit:Int, offset:Int,room: FavouriteRoom, favGiphysLiveData: MutableLiveData<LiveDataResource<List<FavouriteGiphy>>>) {
        doAsync {
            val favour : List<FavouriteGiphy> = room.favouriteDataDao().getGiphys(limit, offset)
            uiThread {
                if(favour.isEmpty()){
                    favGiphysLiveData.value  = LiveDataResource(LiveDataResource.Status.ERROR, 404, null, "No items found")
                }else{
                    favGiphysLiveData.value  = LiveDataResource(LiveDataResource.Status.SUCCESS, 200, favour, null)
                }
            }
        }
    }

    /**
     * In this method add or remove favourite item giphys were fetched from [FavouriteRoom]
     * @param item is the instance of [GiphyObject] or [FavouriteGiphy]
     * @param tag is the tag value
     * @param favouriteRoom is the instance of [FavouriteRoom]
     * @connectToDb method called
     */
    override fun addOrRemoveFavouriteItemFromDb(item:Any, tag:String, favouriteRoom: FavouriteRoom){
        when(item){
            is FavouriteGiphy -> connectToDb(tag, favouriteRoom, item.id, item.image_url)
            is GiphyObject -> connectToDb(tag, favouriteRoom, item.id, item.images.original.url)
        }
    }

    /**
     * In this method connection to room in background thread
     * @param tag is the tag value
     * @param favouriteRoom is the instance of [FavouriteRoom]
     * @param id is the primary key of db
     * @param imageUrl is the image url
     */
    override fun connectToDb(tag: String, favouriteRoom: FavouriteRoom, id: String, imageUrl:String) {
        doAsync {
            if (tag == "false") {
                favouriteRoom.favouriteDataDao()
                    .insert(FavouriteGiphy(id, imageUrl, 1))
            } else {
                favouriteRoom.favouriteDataDao()
                    .deleteFavouriteWithId(id)
            }
        }
    }

    /**
     * In this method favourite id lists are fetched from [FavouriteRoom]
     * @param favouriteRoom is the instance [FavouriteRoom]
     * @param favouriteIds is the instance of MutableLiveData<List<String>>
     */
    override fun getFavouriteIdList(favouriteRoom: FavouriteRoom, favouriteIds: MutableLiveData<List<String>>){
        doAsync {
            val ids = favouriteRoom.favouriteDataDao().getFavouriteAllIds()
            uiThread {
                favouriteIds.value = ids
            }
        }
    }
}

interface GiphyRepoContract{
    fun getTrendingGiphys(limit: Int, offSet: Int, trendingGiphys: MutableLiveData<LiveDataResource<ResponseModel>>)
    fun getSearchGiphys(query:String, limit: Int, offSet: Int, searchGiphys: MutableLiveData<LiveDataResource<ResponseModel>>)
    fun getFavouriteGiphys(limit:Int, offset:Int,room: FavouriteRoom, favGiphysLiveData: MutableLiveData<LiveDataResource<List<FavouriteGiphy>>>)
    fun addOrRemoveFavouriteItemFromDb(item:Any, tag:String, favouriteRoom: FavouriteRoom)
    fun connectToDb(tag: String, favouriteRoom: FavouriteRoom, id: String, imageUrl:String)
    fun getFavouriteIdList(favouriteRoom: FavouriteRoom, favouriteIds: MutableLiveData<List<String>>)

}