package com.mikeschvedov.whatshouldiwatch.data.remote.networking

import com.mikeschvedov.whatshouldiwatch.models.response.*

// all the apis in one facade
interface RemoteApi {
    //Movies
    suspend fun popularMovies(page: Int = 1): Result<ItemWrapper<Movie>>
    suspend fun topRatedMovies(page: Int = 1): Result<ItemWrapper<Movie>>
    suspend fun latestMovies(page: Int = 1): Result<ItemWrapper<Movie>>
    suspend fun searchMovies(page: Int = 1, query: String): Result<ItemWrapper<Movie>>
    suspend fun movieTrailers(movieId: Int): Result<VideoWrapper>
    suspend fun movieCredits(movieId: Int): Result<CreditWrapper>
    suspend fun nowPlayingMovies(page: Int = 1): Result<ItemWrapper<Movie>>
    suspend fun genres(): Result<GenreWrapper>
    //TV
    suspend fun popularTvShows(page: Int = 1): Result<ItemWrapper<TVShow>>
    suspend fun topRatedTvShows(page: Int = 1): Result<ItemWrapper<TVShow>>
    suspend fun latestTvShows(page: Int = 1): Result<ItemWrapper<TVShow>>
    suspend fun searchTvShows(page: Int = 1, query: String): Result<ItemWrapper<TVShow>>
    suspend fun tvShowTrailers(tvId: Int): Result<VideoWrapper>
    suspend fun tvShowCredits(tvId: Int): Result<CreditWrapper>

    //Person
    suspend fun getPerson(personId: Int): Result<Person>
}