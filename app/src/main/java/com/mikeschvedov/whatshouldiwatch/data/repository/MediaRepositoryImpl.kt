package com.mikeschvedov.whatshouldiwatch.data.repository

import androidx.lifecycle.LiveData
import com.mikeschvedov.whatshouldiwatch.data.local.database.daos.MediaDao
import com.mikeschvedov.whatshouldiwatch.models.response.*
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val mediaDao: MediaDao
) : MediaRepository {

    // -- Movies -- //
    override suspend fun addMovies(movies: List<Movie>) = mediaDao.addMovies(movies)
    override suspend fun getMovies(): LiveData<List<MovieWithCategory>> = mediaDao.getMovies()
    override suspend fun deleteAllMovies() = mediaDao.deleteAllMovies()

    override suspend fun addMovieWithCategoryCrossRef(crossRef: MovieCategoryCrossRef) = mediaDao.addMovieCategoryCrossRef(crossRef)
    override suspend fun addMovieWithCategory(movie: Movie, category: Category) = mediaDao.addMovieWithCategory(movie, category)

    // -- Category -- //
    override suspend fun addCategory(category: Category) = mediaDao.addCategory(category)
    override suspend fun getCategories(): LiveData<List<Category>> = mediaDao.getCategories()

    // -- Tv Show -- //
    override suspend fun addTvShows(tvShows: List<TVShow>) = mediaDao.addTvShows(tvShows)
    override suspend fun getTvShows(): LiveData<List<TvShowWithCategory>>  = mediaDao.getTvShows()
    override suspend fun deleteAllTvShows() = mediaDao.deleteAllTvShows()

    override suspend fun addTvShowWithCategoryCrossRef(crossRef: TvShowCategoryCrossRef) = mediaDao.addTvShowCategoryCrossRef(crossRef)
    override suspend fun addTvShowWithCategory(tvShow: TVShow, category: Category) = mediaDao.addTvShowWithCategory(tvShow, category)

}