package com.mikeschvedov.whatshouldiwatch.ui.search


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeschvedov.whatshouldiwatch.models.response.Failure
import com.mikeschvedov.whatshouldiwatch.models.response.Success
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import com.mikeschvedov.whatshouldiwatch.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.utils.Event
import com.mikeschvedov.whatshouldiwatch.utils.ExceptionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {


    @Inject
    lateinit var remoteApi: RemoteApi

    private val searchAdapter = SearchAdapter() {
        userClicksOnItemButton(it)

    }

    //Single Event LiveData to notify fragment about the item that has been clicked
    private val _navigateToDetails = MutableLiveData<Event<Int>>()
    val navigateToDetails: LiveData<Event<Int>> get() = _navigateToDetails

    // Result list
    private val _resultList = MutableLiveData<List<TmdbItem>>()
    val resultList: LiveData<List<TmdbItem>> = _resultList

    // Exception Error
    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    // Fetching Progress Status
    private var _fetchingIsProgress = MutableLiveData<Boolean>()
    val fetchingIsProgress: LiveData<Boolean> get() = _fetchingIsProgress

    // Gets called on the search adapter callback
    private fun userClicksOnItemButton(itemId: Int) {
        _navigateToDetails.value =
            Event(itemId)  // Trigger the event by setting a new Event as a new value
    }

    fun sendMovieSearchRequest(query: String) {
        // Setting empty error message
        _errorMessage.postValue(Event(""))
        // setting to true
        _fetchingIsProgress.postValue(true)
        viewModelScope.launch {
            // Getting full result lift from api
            val result = remoteApi.searchMovies(1, query)
            // If we got a success
            if (result is Success) {
                // Get all items
                val res = result.data.items
                // Get only item that have a poster
                val filteredByNoPosterList = res.filter { s -> s.posterPath != null }
                // setting the category to livedata
                _resultList.postValue(filteredByNoPosterList)
                // setting to false
                _fetchingIsProgress.postValue(false)
            } else if (result is Failure) {
                // handling error -  we map the throwable to our own exception
                _errorMessage.value = Event(
                    ExceptionHandler.getExceptionMessage(
                        ExceptionHandler.mappingExceptions(
                            result.exc
                        )
                    )
                )
                // setting to false
                _fetchingIsProgress.postValue(false)
            }
        }
    }

    fun sendSeriesSearchRequest(query: String) {
        /* Handling error messages */
        // Setting empty error message
        _errorMessage.postValue(Event(""))
        // setting to true
        _fetchingIsProgress.postValue(true)
        /* Creating the Request */
        viewModelScope.launch {
            val result = remoteApi.searchTvShows(1, query)

            if (result is Success) {
                // Get all items
                val res = result.data.items
                // Get only item that have a poster
                val filteredByNoPosterList = res.filter { s -> s.posterPath != null }
                // setting the category to livedata
                _resultList.postValue(filteredByNoPosterList)
                // setting to false
                _fetchingIsProgress.postValue(false)
            } else if (result is Failure) {
                // handling error -  we map the throwable to our own exception
                _errorMessage.value = Event(
                    ExceptionHandler.getExceptionMessage(
                        ExceptionHandler.mappingExceptions(
                            result.exc
                        )
                    )
                )
                /* Handling error messages */
                // setting to false
                _fetchingIsProgress.postValue(false)
            }
        }
    }

    fun getAdapter(): SearchAdapter {
        return searchAdapter
    }

}