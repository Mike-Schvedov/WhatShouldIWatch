package com.mikeschvedov.whatshouldiwatch.ui.home.inner.movies

import androidx.lifecycle.*
import com.mikeschvedov.whatshouldiwatch.models.adapters.CategoryModel
import com.mikeschvedov.whatshouldiwatch.models.response.*
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.data.repository.MediaRepository
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.ParentAdapter
import com.mikeschvedov.whatshouldiwatch.utils.Constants
import com.mikeschvedov.whatshouldiwatch.utils.Event
import com.mikeschvedov.whatshouldiwatch.utils.ExceptionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisplayMoviesViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var mediaRepository: MediaRepository

    private val parentAdapter = ParentAdapter() { item ->
        userClicksOnItemButton(item)
    }

    @Inject
    lateinit var remoteApi: RemoteApi

    //Single Event LiveData to notify fragment about the item that has been clicked
    private val _navigateToDetails = MutableLiveData<Event<TmdbItem>>()
    val navigateToDetails: LiveData<Event<TmdbItem>> get() = _navigateToDetails

    // Top Rated Movies
    private val _topRatedMovieList = MutableLiveData<CategoryModel>()
    val topRatedMovieList: LiveData<CategoryModel> = _topRatedMovieList

    // New Releases Movies
    private val _newReleasesMovieList = MutableLiveData<CategoryModel>()
    val newReleasesMovieList: LiveData<CategoryModel> = _newReleasesMovieList

    // Popular Movies
    private val _popularMovieList = MutableLiveData<CategoryModel>()
    val popularMovieList: LiveData<CategoryModel> = _popularMovieList

    // Now Playing Movies
    private val _nowPlayingMovieList = MutableLiveData<CategoryModel>()
    val nowPlayingMovieList: LiveData<CategoryModel> = _nowPlayingMovieList

    // Exception Error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Success Flag
    private val _successFlag = MutableLiveData<Boolean>()
    val successFlag: LiveData<Boolean> get() = _successFlag

    // Gets called on the search adapter callback
    private fun userClicksOnItemButton(item: TmdbItem) {
        _navigateToDetails.value =
            Event(item)  // Trigger the event by setting a new Event as a new value
    }

    // Get data from database
    fun getDataFromDatabase() {
        //  getAllDataFromApi()  //---> TODO: update database from api - test if really works
        // Now we will get the data from the database
        getNewReleasesMoviesFromDB()
        getPopularMoviesFromDB()
        getTopRatedMoviesFromDB()
        getNowPlayingMoviesFromDB()
        _successFlag.postValue(true)
    }

    //TODO : refactor all repeating code into a generic method

    // Fetching data from database
    private fun getPopularMoviesFromDB() {
        viewModelScope.launch {
            val listToUpdate: MutableList<TmdbItem> = mutableListOf()
            // We get all movies from database
            mediaRepository.getMovies().observeForever() { listOfMovies ->
                // We loop over all the movies in the database
                listOfMovies.forEach { movieWithCategory ->
                    // For each movie, we loop over the associated categories
                    movieWithCategory.categories.forEach { category ->
                        // If our relevant category is one of his associated categories,
                        // add him to the list
                        if (category.categoryName == Constants.POPULAR_MOVIES) {
                            listToUpdate.add(movieWithCategory.movie)
                        }
                    }
                    _popularMovieList.postValue(
                        CategoryModel(
                            Constants.POPULAR_MOVIES,
                            listToUpdate
                        )
                    )
                }
            }
        }
    }

    // Fetching data from database
    private fun getNewReleasesMoviesFromDB() {
        viewModelScope.launch {
            val listToUpdate: MutableList<TmdbItem> = mutableListOf()
            // We get all movies from database
            mediaRepository.getMovies().observeForever() { listOfMovies ->
                // We loop over all the movies in the database
                listOfMovies.forEach { movieWithCategory ->
                    // For each movie, we loop over the associated categories
                    movieWithCategory.categories.forEach { category ->
                        // If our relevant category is one of his associated categories,
                        // add him to the list
                        if (category.categoryName == Constants.NEW_RELEASES_MOVIES) {
                            listToUpdate.add(movieWithCategory.movie)
                        }
                    }
                    _newReleasesMovieList.postValue(
                        CategoryModel(
                            Constants.NEW_RELEASES_MOVIES,
                            listToUpdate
                        )
                    )
                }
            }
        }
    }

    // Fetching data from database
    private fun getTopRatedMoviesFromDB() {
        viewModelScope.launch {
            val listToUpdate: MutableList<TmdbItem> = mutableListOf()
            // We get all movies from database
            mediaRepository.getMovies().observeForever() { listOfMovies ->
                // We loop over all the movies in the database
                listOfMovies.forEach { movieWithCategory ->
                    // For each movie, we loop over the associated categories
                    movieWithCategory.categories.forEach { category ->
                        // If our relevant category is one of his associated categories,
                        // add him to the list
                        if (category.categoryName == Constants.TOP_RATED_MOVIES) {
                            listToUpdate.add(movieWithCategory.movie)
                        }
                    }
                    _topRatedMovieList.postValue(
                        CategoryModel(
                            Constants.TOP_RATED_MOVIES,
                            listToUpdate
                        )
                    )
                }
            }
        }
    }

    // Fetching data from database
    private fun getNowPlayingMoviesFromDB() {
        viewModelScope.launch {
            val listToUpdate: MutableList<TmdbItem> = mutableListOf()
            // We get all movies from database
            mediaRepository.getMovies().observeForever() { listOfMovies ->
                // We loop over all the movies in the database
                listOfMovies.forEach { movieWithCategory ->
                    // For each movie, we loop over the associated categories
                    movieWithCategory.categories.forEach { category ->
                        // If our relevant category is one of his associated categories,
                        // add him to the list
                        if (category.categoryName == Constants.NOW_PLAYING_MOVIES) {
                            listToUpdate.add(movieWithCategory.movie)
                        }
                    }
                    _nowPlayingMovieList.postValue(
                        CategoryModel(
                            Constants.NOW_PLAYING_MOVIES,
                            listToUpdate
                        )
                    )
                }
            }
        }
    }

    fun getAdapter(): ParentAdapter {
        return parentAdapter
    }



    private fun getAllDataFromApi() {

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

                mediaRepository.addCategory(Category(1, Constants.POPULAR_MOVIES))

                // put new data
                popularMovies.data.items.forEach { movie->
                    mediaRepository.addMovieWithCategoryCrossRef(
                        MovieCategoryCrossRef(movie.id, 1)
                    )
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

                mediaRepository.addCategory(Category(2, Constants.TOP_RATED_MOVIES))

                // put new data
                topRatedMovies.data.items.forEach { movie->
                    mediaRepository.addMovieWithCategoryCrossRef(
                        MovieCategoryCrossRef(movie.id, 2)
                    )
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
                mediaRepository.addCategory(Category(3, Constants.NEW_RELEASES_MOVIES))

                // put new data
                latestMovies.data.items.forEach { movie->
                    mediaRepository.addMovieWithCategoryCrossRef(
                        MovieCategoryCrossRef(movie.id, 3)
                    )
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
                mediaRepository.addCategory(Category(4, Constants.NOW_PLAYING_MOVIES))

                // put new data
                nowPlayingMovies.data.items.forEach { movie->
                    mediaRepository.addMovieWithCategoryCrossRef(
                        MovieCategoryCrossRef(movie.id, 4)
                    )
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
}
