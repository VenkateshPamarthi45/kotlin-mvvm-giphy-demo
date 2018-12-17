package com.venkateshpamarthi.gipyapp.db

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.support.annotation.NonNull

@Entity(tableName = "favourite_giphy")
data class FavouriteGiphy(
    @PrimaryKey
    @NonNull
    var id : String,
    @ColumnInfo(name = "image_url")
    var image_url : String,
    @ColumnInfo(name = "is_favourite")
    var is_favourite : Int
)

@Dao
interface FavouriteDataDao {

    @Query("SELECT * from favourite_giphy LIMIT :limit OFFSET :offset")
    fun getGiphys(limit:Int, offset:Int): List<FavouriteGiphy>

    @Query("SELECT id from favourite_giphy")
    fun getFavouriteAllIds(): List<String>

    @Insert(onConflict = REPLACE)
    fun insert(favouriteData: FavouriteGiphy)

    @Query("DELETE from favourite_giphy where id= :id")
    fun deleteFavouriteWithId(id:String)
}