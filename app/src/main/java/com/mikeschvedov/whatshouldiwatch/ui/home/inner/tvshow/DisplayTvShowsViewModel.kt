package com.mikeschvedov.whatshouldiwatch.ui.home.inner.tvshow

import androidx.lifecycle.*
import com.mikeschvedov.whatshouldiwatch.models.adapters.CategoryModel
import com.mikeschvedov.whatshouldiwatch.models.adapters.ChildModel
import com.mikeschvedov.whatshouldiwatch.models.response.*
import com.mikeschvedov.whatshouldiwatch.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.ParentAdapter
import com.mikeschvedov.whatshouldiwatch.utils.Constants
import com.mikeschvedov.whatshouldiwatch.utils.Event
import com.mikeschvedov.whatshouldiwatch.utils.ExceptionHandler.Companion.getExceptionMessage
import com.mikeschvedov.whatshouldiwatch.utils.ExceptionHandler.Companion.mappingExceptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisplayTvShowsViewModel @Inject constructor() : ViewModel() {

    private val parentAdapter = ParentAdapter(){item->
        userClicksOnItemButton(item)
    }
    var successIndicator: Boolean = false

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

    private fun getPopularShows() {
        handleResult(Constants.POPULAR_SHOW, _popularShowsList)
    }

    private fun getNewReleasesShows() {
        handleResult(Constants.NEW_RELEASES_SHOW, _newReleasesShowsList)
    }

    private fun topRatedShows() {
        handleResult(Constants.TOP_RATED_SHOW, _topRatedShowsList)
    }

    private fun handleResult(
        endpointName: String,
        listToUpdate: MutableLiveData<CategoryModel>
    ) {
        //false at beginning of call
        successIndicator = false
        _successFlag.postValue(successIndicator)

        viewModelScope.launch {
            // val result = remoteApi.latestMovies()
            val result = decideShowEndPoint(endpointName)

            if (result is Success) {
                // if the response is successful create a category
                val category = createCategory(result.data.items, endpointName)
                // setting the category to livedata
                listToUpdate.postValue(category)
                // true when we have success
                successIndicator = true
                _successFlag.postValue(successIndicator)
            } else if (result is Failure) {
                // handling error
                // we map the throwable to our own exception
                _errorMessage.postValue(getExceptionMessage(mappingExceptions(result.exc)))
            }
        }
    }

    private suspend fun decideShowEndPoint(endpoint: String): Result<ItemWrapper<TVShow>> {
        return when(endpoint){
            Constants.POPULAR_SHOW -> remoteApi.popularTvShows()
            Constants.NEW_RELEASES_SHOW -> remoteApi.latestTvShows()
            Constants.TOP_RATED_SHOW -> remoteApi.topRatedTvShows()
            else -> remoteApi.popularTvShows()
        }
    }

    fun getAdapter(): ParentAdapter {
        return parentAdapter
    }

    private fun createCategory(list: List<TVShow>, category: String): CategoryModel =
        CategoryModel(category, list)

    fun sendApiRequests() {
        getPopularShows()
        topRatedShows()
        getNewReleasesShows()
    }
}