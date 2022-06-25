package com.mikeschvedov.whatshouldiwatch.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.data.repository.MediaRepository
import com.mikeschvedov.whatshouldiwatch.models.response.*
import com.mikeschvedov.whatshouldiwatch.utils.Constants
import com.mikeschvedov.whatshouldiwatch.utils.ExceptionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var remoteApi: RemoteApi

    @Inject
    lateinit var mediaRepository: MediaRepository

    /* --- LiveData --- */
    // Exception Error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getAllDataFromMoviesApi() {

        viewModelScope.launch(Dispatchers.IO){

            /* --- Fetch data from the api --- */

            val popularMovies = remoteApi.popularMovies()
            val topRatedMovies = remoteApi.topRatedMovies()
            val latestMovies = remoteApi.latestMovies()
            val nowPlayingMovies = remoteApi.nowPlayingMovies()

            /* --- Store in local database --- */

            //TODO : refactor all repeating code into a generic method

            // ------ Popular ------ //
            if (popularMovies is Success) {
                // put new data
                popularMovies.data.items.forEach { movie->
                    mediaRepository.addMovieWithCategory(movie, Category(1, Constants.POPULAR_MOVIES))
                }
            } else if (popularMovies is Failure) {
                _errorMessage.postValue(
                    ExceptionHandler.getExceptionMessage(
                        ExceptionHandler.mappingExceptions(
                            popularMovies.exc
                        )
                    )
                )
            }

            // ------ Top Rated ------ //
            if (topRatedMovies is Success) {
                // put new data
                topRatedMovies.data.items.forEach { movie->
                    mediaRepository.addMovieWithCategory(movie, Category(2, Constants.TOP_RATED_MOVIES))
                }
            } else if (topRatedMovies is Failure) {
                _errorMessage.postValue(
                    ExceptionHandler.getExceptionMessage(
                        ExceptionHandler.mappingExceptions(
                            topRatedMovies.exc
                        )
                    )
                )
            }

            // ------ New Releases ------ //
            if (latestMovies is Success) {
                // put new data
                latestMovies.data.items.forEach { movie->
                    mediaRepository.addMovieWithCategory(movie, Category(3, Constants.NEW_RELEASES_MOVIES))
                }
            } else if (latestMovies is Failure) {
                _errorMessage.postValue(
                    ExceptionHandler.getExceptionMessage(
                        ExceptionHandler.mappingExceptions(
                            latestMovies.exc
                        )
                    )
                )
            }

            // ------ Now Playing ------ //
            if (nowPlayingMovies is Success) {
                // put new data
                nowPlayingMovies.data.items.forEach { movie->
                    mediaRepository.addMovieWithCategory(movie, Category(4, Constants.NOW_PLAYING_MOVIES))
                }
            } else if (nowPlayingMovies is Failure) {
                _errorMessage.postValue(
                    ExceptionHandler.getExceptionMessage(
                        ExceptionHandler.mappingExceptions(
                            nowPlayingMovies.exc
                        )
                    )
                )
            }

        }
    }

    fun getAllDataFromTvShowsApi() {

        viewModelScope.launch(Dispatchers.IO){

            /* --- Fetch data from the api --- */

            val popularShows = remoteApi.popularTvShows()
            val topRatedShows = remoteApi.topRatedTvShows()
            val latestShows = remoteApi.latestTvShows()


            /* --- Store in local database --- */

            //TODO : refactor all repeating code into a generic method

            // ------ Popular ------ //
            if (popularShows is Success) {
                // put new data
                popularShows.data.items.forEach { show->
                    mediaRepository.addTvShowWithCategory(show, Category(5, Constants.POPULAR_SHOW))
                }
            } else if (popularShows is Failure) {
                _errorMessage.postValue(
                    ExceptionHandler.getExceptionMessage(
                        ExceptionHandler.mappingExceptions(
                            popularShows.exc
                        )
                    )
                )
            }

            // ------ Top Rated ------ //
            if (topRatedShows is Success) {
                // put new data
                topRatedShows.data.items.forEach { show->
                    mediaRepository.addTvShowWithCategory(show, Category(6, Constants.TOP_RATED_SHOW))
                }
            } else if (topRatedShows is Failure) {
                _errorMessage.postValue(
                    ExceptionHandler.getExceptionMessage(
                        ExceptionHandler.mappingExceptions(
                            topRatedShows.exc
                        )
                    )
                )
            }

            // ------ New Releases ------ //
            if (latestShows is Success) {
                // put new data
                latestShows.data.items.forEach { show->
                    mediaRepository.addTvShowWithCategory(show, Category(7, Constants.NEW_RELEASES_SHOW))
                }
            } else if (latestShows is Failure) {
                _errorMessage.postValue(
                    ExceptionHandler.getExceptionMessage(
                        ExceptionHandler.mappingExceptions(
                            latestShows.exc
                        )
                    )
                )
            }


        }
    }
}

