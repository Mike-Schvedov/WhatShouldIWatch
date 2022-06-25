package com.mikeschvedov.whatshouldiwatch.di

import android.content.Context
import androidx.room.Room
import com.mikeschvedov.whatshouldiwatch.data.local.database.MovieDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    const val DBName = "MovieDB"

    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(appContext, MovieDB::class.java, DBName)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMovieDao(movieDB: MovieDB) =
        movieDB.movieDao()


}