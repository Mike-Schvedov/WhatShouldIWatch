package com.mikeschvedov.whatshouldiwatch.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mikeschvedov.whatshouldiwatch.data.local.database.daos.MediaDao
import com.mikeschvedov.whatshouldiwatch.models.response.*

@Database(entities = [Movie::class, Category::class, MovieCategoryCrossRef::class, TVShow::class, TvShowCategoryCrossRef::class], version = 1)
abstract class MovieDB: RoomDatabase() {
    abstract fun movieDao(): MediaDao
}