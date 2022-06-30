package com.mikeschvedov.whatshouldiwatch.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.mikeschvedov.whatshouldiwatch.models.adapters.MediaGroup
import com.mikeschvedov.whatshouldiwatch.models.adapters.MediaGroupList
import com.mikeschvedov.whatshouldiwatch.models.response.Movie
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import kotlinx.coroutines.flow.Flow

interface ContentMediator {
    // Movies //
    suspend fun updateDatabaseViaApi()
    suspend fun getMoviesPageMedia(): Flow<MediaGroupList>
    fun getMoviesByCategory(categoryId: Long): MediaGroup

    // Tv Shows //
    suspend fun getTvShowsPageMedia(): Flow<MediaGroupList>

    fun getTopRatedWithPaging(): Flow<PagingData<TmdbItem>>

    suspend fun getSearchMoviesPageMedia(query: String): Flow<List<TmdbItem>>
    suspend fun getSearchTvShowsPageMedia(query: String) : Flow<List<TmdbItem>>

    // fun getTVShowByCategory(): MediaGroup
    // fun getFavoriteMovies(): MediaGroup
    // suspend fun addMovieToFavorite(movie: Movie)
    // suspend fun removeMovieFromFavorite(movie: Movie)
    //fun isMovieInFavoriteTable(movie: Movie): Boolean

    // suspend fun getMovieTrailers(movieId: Int): Result<VideoWrapper>
    //suspend fun searchMovies(page: Int = 1, query: String): Result<ItemWrapper<Movie>>

}