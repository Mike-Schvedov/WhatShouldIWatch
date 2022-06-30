package com.mikeschvedov.whatshouldiwatch.data.repository

import androidx.lifecycle.LiveData
import com.mikeschvedov.whatshouldiwatch.models.response.*
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
     // -- Movies -- //
     suspend fun addMovies(movies: List<Movie>)
     suspend fun getMovies() : LiveData<List<MovieWithCategory>>
     suspend fun deleteAllMovies()

     suspend fun addMovieWithCategoryCrossRef(crossRef: MovieCategoryCrossRef)
     suspend fun addMovieWithCategory(movie: Movie, category: Category)

     // -- Category -- //
     suspend fun addCategory(category: Category)
     suspend fun getCategories(): LiveData<List<Category>>
     fun getMoviesByCategory(categoryId: Long) : CategoryWithMovies

     fun getCategoryWithMovies(): List<CategoryWithMovies>
     fun getCategoryWithTvShows(): Flow<List<CategoryWithTvShows>>


     // -- TvShow -- //
     suspend fun addTvShows(tvShows: List<TVShow>)
     suspend fun getTvShows() : LiveData<List<TvShowWithCategory>>
     suspend fun deleteAllTvShows()

     suspend fun addTvShowWithCategoryCrossRef(crossRef: TvShowCategoryCrossRef)
     suspend fun addTvShowWithCategory(tvShow: TVShow, category: Category)

}