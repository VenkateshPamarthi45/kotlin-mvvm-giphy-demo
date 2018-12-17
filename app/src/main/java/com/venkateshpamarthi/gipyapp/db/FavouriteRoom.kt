package com.venkateshpamarthi.gipyapp.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context


@Database(entities = arrayOf(FavouriteGiphy::class), version = 1, exportSchema = false)
abstract class FavouriteRoom : RoomDatabase() {

    abstract fun favouriteDataDao(): FavouriteDataDao

    companion object {
        private var INSTANCE: FavouriteRoom? = null

        fun getInstance(context: Context): FavouriteRoom? {
            if (INSTANCE == null) {
                synchronized(FavouriteRoom::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        FavouriteRoom::class.java, "giphy.db")
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}