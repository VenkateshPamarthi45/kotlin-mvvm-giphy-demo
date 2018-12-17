package com.venkateshpamarthi.gipyapp.network

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyNetworkManager {

    @GET("trending")
    fun getTrending(@Query("api_key") apiKey: String?, @Query("limit") limit: Int?, @Query("offset") offset: Int?): Observable<ResponseModel>

    @GET("search")
    fun getSearch(@Query("api_key") apiKey: String?, @Query("q") query: String, @Query("limit") limit: Int?, @Query("offset") offset: Int?): Observable<ResponseModel>

    companion object Factory {
        fun create(): GiphyNetworkManager {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.giphy.com/v1/gifs/")
                .build()

            return retrofit.create(GiphyNetworkManager::class.java)
        }
    }
}