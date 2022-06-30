package com.mikeschvedov.whatshouldiwatch.ui.search


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeschvedov.whatshouldiwatch.models.response.Failure
import com.mikeschvedov.whatshouldiwatch.models.response.Success
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.data.repository.ContentMediator
import com.mikeschvedov.whatshouldiwatch.models.adapters.MediaGroupList
import com.mikeschvedov.whatshouldiwatch.models.response.TVShow
import com.mikeschvedov.whatshouldiwatch.utils.ExceptionHandler
import com.mikeschvedov.whatshouldiwatch.utils.ItemListWrapper
import com.mikeschvedov.whatshouldiwatch.utils.ItemWrapper
import com.mikeschvedov.whatshouldiwatch.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val contentMediator: ContentMediator
) : ViewModel() {

    @Inject
    lateinit var remoteApi: RemoteApi

    private val searchAdapter = SearchAdapter()

    private val _clicked = MutableSharedFlow<TmdbItem?>(replay = 0)
    val clicked: SharedFlow<TmdbItem?> = _clicked


    // Result list
    private var _searchFullMediaList = MutableStateFlow(ItemListWrapper())
    val searchFullMediaList = _searchFullMediaList.asStateFlow()

    init {
        viewModelScope.launch {
            searchAdapter.itemHotFlow
                .map { itemWithWrapper ->
                    itemWithWrapper.item
                }
                .collect { item ->
                    _clicked.emit(item)
                }
        }
    }

    fun sendMoviesSearchRequest(query: String) {
        viewModelScope.launch {
            contentMediator.getSearchMoviesPageMedia(query)
                .flowOn(Dispatchers.IO)
                .collect { searchResult ->
                    _searchFullMediaList.value = ItemListWrapper(searchResult)
                }
        }
    }

    fun sendSeriesSearchRequest(query: String) {
        viewModelScope.launch {
            contentMediator.getSearchTvShowsPageMedia(query)
                .flowOn(Dispatchers.IO)
                .collect { searchResult ->
                    _searchFullMediaList.value = ItemListWrapper(searchResult)
                }
        }
    }

    fun getAdapter(): SearchAdapter {
        return searchAdapter
    }

}