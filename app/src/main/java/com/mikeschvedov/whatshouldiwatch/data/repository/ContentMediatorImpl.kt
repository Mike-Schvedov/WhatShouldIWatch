package com.mikeschvedov.whatshouldiwatch.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.models.adapters.MediaGroup
import com.mikeschvedov.whatshouldiwatch.models.adapters.MediaGroupList
import com.mikeschvedov.whatshouldiwatch.models.response.*
import com.mikeschvedov.whatshouldiwatch.utils.CATEGORY
import com.mikeschvedov.whatshouldiwatch.utils.ExceptionHandler
import com.mikeschvedov.whatshouldiwatch.utils.SingleEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


class ContentMediatorImpl @Inject constructor(
    private val remoteApi: RemoteApi,
    private var mediaRepository: MediaRepository
) : ContentMediator {


    /* Update Database from Api */
    override suspend fun updateDatabaseViaApi() {
        // Getting all lists from Api
        val popularResult = remoteApi.popularMovies()
        val latestResult = remoteApi.latestMovies()
        val topRated = remoteApi.topRatedMovies()
        val nowPlayingResult = remoteApi.nowPlayingMovies()

        val popularShowsResult = remoteApi.popularTvShows()
        val topRatedShowsResult = remoteApi.topRatedTvShows()
        val newReleasedShowsResult = remoteApi.latestTvShows()

        // Storing in local Database
        if (popularResult is Success && latestResult is Success && topRated is Success && nowPlayingResult is Success
            && popularShowsResult is Success && topRatedShowsResult is Success && newReleasedShowsResult is Success
        ) {
            // ------------ Add movies and movies categories into the DB
            addMoviesToDB(
                popularResult.data,
                Category(CATEGORY.POPULAR_MOVIES.first, CATEGORY.POPULAR_MOVIES.second)
            )
            addMoviesToDB(
                latestResult.data,
                Category(CATEGORY.NEW_RELEASED_MOVIES.first, CATEGORY.NEW_RELEASED_MOVIES.second)
            )
            addMoviesToDB(
                topRated.data,
                Category(CATEGORY.TOP_RATED_MOVIES.first, CATEGORY.TOP_RATED_MOVIES.second)
            )
            addMoviesToDB(
                nowPlayingResult.data,
                Category(CATEGORY.NOW_PLAYING_MOVIES.first, CATEGORY.NOW_PLAYING_MOVIES.second)
            )
            // ------------ Add shows and shows categories into the DB
            addShowsToDB(
                popularShowsResult.data,
                Category(CATEGORY.POPULAR_SHOWS.first, CATEGORY.POPULAR_SHOWS.second)
            )
            addShowsToDB(
                topRatedShowsResult.data,
                Category(CATEGORY.TOP_RATED_SHOWS.first, CATEGORY.TOP_RATED_SHOWS.second)
            )
            addShowsToDB(
                newReleasedShowsResult.data,
                Category(CATEGORY.NEW_RELEASED_SHOWS.first, CATEGORY.NEW_RELEASED_SHOWS.second)
            )
        } else if (popularResult is Failure) {
            //TODO handle errors..
        }
    }

    /* Get Movies data to display from DATABASE  */
    // Getting all the data needed (From Database) to be displayed on main movies page.
    override suspend fun getMoviesPageMedia() = flow {
        val tempList = MediaGroupList()
        // Loop over all categories in the database
        mediaRepository.getCategoryWithMovies().forEach {
            // If the category is NOT containing Tv than it belongs to moveis
            if (!it.category.categoryName.contains("Tv")) {
                // First we add the category name to the media group
                val temp = MediaGroup(it.category.categoryName)
                // We loop over all movies in this category
                it.movies.forEach { movie ->
                    //movie.isFavorite = isMovieInFavoriteTable(movie)
                    temp.childModelList.add(movie as TmdbItem)
                }
                tempList.list.add(temp)
            }
        }
        emit(tempList)
    }

    /* Get Tv Shows data to display from DATABASE  */
    // Getting all the data needed (From Database) to be displayed on main Shows page.
    override suspend fun getTvShowsPageMedia() = flow {
        val tempList = MediaGroupList()
        // Loop over all categories in the database
        mediaRepository.getCategoryWithTvShows()
            .flowOn(Dispatchers.IO)
            .collect { list ->
                list.forEach { categoryWithShow ->
                    // If the category is containing Tv than it belongs to tv shows
                    if (categoryWithShow.category.categoryName.contains("Tv")) {
                        // First we add the category name to the media group
                        val temp = MediaGroup(categoryWithShow.category.categoryName)
                        // We loop over all shows in this category
                        categoryWithShow.tvShows.forEach { show ->
                            //movie.isFavorite = isMovieInFavoriteTable(show)
                            temp.childModelList.add(show as TmdbItem)
                        }
                        tempList.list.add(temp)
                    }
                }
            }

        emit(tempList)
    }

    override suspend fun getSearchMoviesPageMedia(query: String) = flow {

        val result = remoteApi.searchMovies(1, query)

        if (result is Success) {
            // Get all items
            val res = result.data.items
            // Get only item that have a poster
            val filteredByNoPosterList = res.filter { s -> s.posterPath != null }
            // setting the category to livedata
            emit(filteredByNoPosterList)
        }
    }

    override suspend fun getSearchTvShowsPageMedia(query: String) = flow {

        val result = remoteApi.searchTvShows(1, query)

        if (result is Success) {
            // Get all items
            val res = result.data.items
            // Get only item that have a poster
            val filteredByNoPosterList = res.filter { s -> s.posterPath != null }
            // setting the category to livedata
            emit(filteredByNoPosterList)
        }
    }


    override fun getMoviesByCategory(categoryId: Long): MediaGroup {
        val categoryWithMovie = mediaRepository.getMoviesByCategory(categoryId)
        val result = MediaGroup(categoryWithMovie.category.categoryName)
        categoryWithMovie.movies.forEach {
            //  it.isFavorite = isMovieInFavoriteTable(it)
            result.childModelList.add(it as TmdbItem)
        }
        return result
    }

    private suspend fun addMoviesToDB(
        movieList: ItemWrapper<Movie>,
        category: Category
    ) {
        // For each movie we get from the database (in this category) add to database, also add category
        movieList.items.forEach { movie ->
            mediaRepository.addMovieWithCategory(movie, category)

        }
    }

    private suspend fun addShowsToDB(
        showList: ItemWrapper<TVShow>,
        category: Category
    ) {
        // For each show we get from the database (in this category) add to database, also add category
        showList.items.forEach { show ->
            mediaRepository.addTvShowWithCategory(show, category)
        }
    }


    // Paging example
    override fun getTopRatedWithPaging(
        //    query: String?, NOT RELEVANT HERE
        //   sortBy: String? NOT RELEVANT HERE
    ): Flow<PagingData<TmdbItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MediaPagingSource(
                    remoteApi,
                    //       query ?: "", NOT RELEVANT HERE
                    //      sortBy NOT RELEVANT HERE
                )
            }
        ).flow
    }


    /*   override fun getFavoriteMovies(): MediaGroup {
           val result =
               MediaGroup(mediaRepository.getMoviesByCategory(CATEGORY.MY_FAVORITES.first).category.name)
           mediaRepository.getMoviesByCategory(CATEGORY.MY_FAVORITES.first).movies.forEach {
               it.isFavorite = true
               result.list.add(it as TmdbItem)
           }
           return result
       }*/


    /*  override fun getTVShowByCategory(): MediaGroup {
          //TODO("Not yet implemented")
          return MediaGroup(CATEGORY.TOP_RATED_MOVIES.second)
      }*/


    /* override suspend fun addMovieToFavorite(movie: Movie) =
         mediaRepository.addMovieWithCategory(
             movie, Category(CATEGORY.MY_FAVORITES.first, CATEGORY.MY_FAVORITES.second)
         )*/

    /* override suspend fun removeMovieFromFavorite(movie: Movie) {
         mediaRepository.removeMovieCrossCategory(movie.id, CATEGORY.MY_FAVORITES.first)
     }*/

    /* override fun isMovieInFavoriteTable(movie: Movie): Boolean =
         mediaRepository.getMovieCategoryRef(movie.id, CATEGORY.MY_FAVORITES.first) != null
 */
    /*  override suspend fun getMovieTrailers(movieId: Int): Result<VideoWrapper> =
          remoteApi.movieTrailers(movieId)
  */

/*
    override suspend fun searchMovies(page: Int, query: String): Result<ItemWrapper<Movie>> {
        //TODO save to latests searches...
        return remoteApi.searchMovies(page, query)
    }
*/


}
