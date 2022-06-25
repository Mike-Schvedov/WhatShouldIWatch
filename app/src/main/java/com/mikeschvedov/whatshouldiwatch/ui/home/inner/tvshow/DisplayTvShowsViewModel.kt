package com.mikeschvedov.whatshouldiwatch.ui.home.inner.tvshow

import androidx.lifecycle.*
import com.mikeschvedov.whatshouldiwatch.models.adapters.CategoryModel
import com.mikeschvedov.whatshouldiwatch.models.response.*
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.data.repository.MediaRepository
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.ParentAdapter
import com.mikeschvedov.whatshouldiwatch.utils.Constants
import com.mikeschvedov.whatshouldiwatch.utils.Event
import com.mikeschvedov.whatshouldiwatch.utils.ExceptionHandler
import com.mikeschvedov.whatshouldiwatch.utils.ExceptionHandler.Companion.getExceptionMessage
import com.mikeschvedov.whatshouldiwatch.utils.ExceptionHandler.Companion.mappingExceptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisplayTvShowsViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var mediaRepository: MediaRepository

    private val parentAdapter = ParentAdapter(){item->
        userClicksOnItemButton(item)
    }

    @Inject
    lateinit var remoteApi: RemoteApi

    //Single Event LiveData to notify fragment about the item that has been clicked
    private val _navigateToDetails = MutableLiveData<Event<TmdbItem>>()
    val navigateToDetails: LiveData<Event<TmdbItem>> get() = _navigateToDetails

    // Top Rated Tv Show
    private val _topRatedShowsList = MutableLiveData<CategoryModel>()
    val topRatedShowsList: LiveData<CategoryModel> = _topRatedShowsList

    // New Releases Tv Show
    private val _newReleasesShowsList = MutableLiveData<CategoryModel>()
    val newReleasesShowsList: LiveData<CategoryModel> = _newReleasesShowsList

    // Popular Tv Show
    private val _popularShowsList = MutableLiveData<CategoryModel>()
    val popularShowsList: LiveData<CategoryModel> = _popularShowsList

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

    //TODO : refactor all repeating code into a generic method

    // Fetching data from database
    private fun getPopularTvShowsFromDB() {
        viewModelScope.launch {
            val listToUpdate: MutableList<TmdbItem> = mutableListOf()
            // We get all movies from database
            mediaRepository.getTvShows().observeForever() { listOfTvShows ->
                // We loop over all the movies in the database
                listOfTvShows.forEach { tvShowWithCategory ->
                    // For each movie, we loop over the associated categories
                    tvShowWithCategory.categories.forEach { category ->
                        // If our relevant category is one of his associated categories,
                        // add him to the list
                        if (category.categoryName == Constants.POPULAR_SHOW) {
                            listToUpdate.add(tvShowWithCategory.tvShow)
                        }
                    }
                    _popularShowsList.postValue(
                        CategoryModel(
                            Constants.POPULAR_SHOW,
                            listToUpdate
                        )
                    )
                }
            }
        }
    }

    // Fetching data from database
    private fun getNewReleasesTvShowsFromDB() {
        viewModelScope.launch {
            val listToUpdate: MutableList<TmdbItem> = mutableListOf()
            // We get all movies from database
            mediaRepository.getTvShows().observeForever() { listOfTvShows ->
                // We loop over all the movies in the database
                listOfTvShows.forEach { tvShowWithCategory ->
                    // For each movie, we loop over the associated categories
                    tvShowWithCategory.categories.forEach { category ->
                        // If our relevant category is one of his associated categories,
                        // add him to the list
                        if (category.categoryName == Constants.NEW_RELEASES_SHOW) {
                            listToUpdate.add(tvShowWithCategory.tvShow)
                        }
                    }
                    _newReleasesShowsList.postValue(
                        CategoryModel(
                            Constants.NEW_RELEASES_SHOW,
                            listToUpdate
                        )
                    )
                }
            }
        }
    }

    // Fetching data from database
    private fun getTopRatedTvShowsFromDB() {
        viewModelScope.launch {
            val listToUpdate: MutableList<TmdbItem> = mutableListOf()
            // We get all movies from database
            mediaRepository.getTvShows().observeForever() { listOfTvShows ->
                // We loop over all the movies in the database
                listOfTvShows.forEach { tvShowWithCategory ->
                    // For each movie, we loop over the associated categories
                    tvShowWithCategory.categories.forEach { category ->
                        // If our relevant category is one of his associated categories,
                        // add him to the list
                        if (category.categoryName == Constants.TOP_RATED_SHOW) {
                            listToUpdate.add(tvShowWithCategory.tvShow)
                        }
                    }
                    _topRatedShowsList.postValue(
                        CategoryModel(
                            Constants.TOP_RATED_SHOW,
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

    // Get data from database
    fun getDataFromDatabase() {
     //   getAllDataFromApi()  //---> TODO: update database from api - test if really works
        // Now we will get the data from the database
        getPopularTvShowsFromDB()
        getNewReleasesTvShowsFromDB()
        getTopRatedTvShowsFromDB()

        _successFlag.postValue(true)
    }

}