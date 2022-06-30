package com.mikeschvedov.whatshouldiwatch.ui.home.inner.movies

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mikeschvedov.whatshouldiwatch.models.response.*
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.data.repository.ContentMediator
import com.mikeschvedov.whatshouldiwatch.data.repository.MediaRepository
import com.mikeschvedov.whatshouldiwatch.models.adapters.MediaGroupList
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.ParentAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisplayMoviesViewModel @Inject constructor(
    private val contentMediator: ContentMediator
) :
    ViewModel() {

    @Inject
    lateinit var mediaRepository: MediaRepository
    @Inject
    lateinit var remoteApi: RemoteApi

    private val parentAdapter = ParentAdapter()

    private val _clicked = MutableSharedFlow<TmdbItem?>(replay = 0)
    val clicked: SharedFlow<TmdbItem?> = _clicked

    private var _moviesFullMediaList = MutableStateFlow(MediaGroupList())
    val moviesFullMediaList = _moviesFullMediaList.asStateFlow()

    init {
        viewModelScope.launch {
            parentAdapter.itemHotFlow
                .map { itemWithWrapper ->
                    itemWithWrapper.item
                }
                .collect { item ->
                    _clicked.emit(item)
                }
        }
    }


    fun getAdapter(): ParentAdapter {
        return parentAdapter
    }

    fun fetchMoviesPageMedia() {
        viewModelScope.launch {
            contentMediator.getMoviesPageMedia()
                .flowOn(Dispatchers.IO)
                .collect { mediaGroupList ->
                    _moviesFullMediaList.value = mediaGroupList
                }
        }
    }

    fun updateDBAndFetchMovies() {
        viewModelScope.launch {
            contentMediator.updateDatabaseViaApi()
            contentMediator.getMoviesPageMedia()
                .flowOn(Dispatchers.IO)
                .collect { mediaGroupList ->
                    _moviesFullMediaList.value = mediaGroupList
                }
        }
    }

    fun getTopRatedByPaging(): Flow<PagingData<TmdbItem>> {
        return contentMediator.getTopRatedWithPaging()
            .cachedIn(viewModelScope)
    }

}
