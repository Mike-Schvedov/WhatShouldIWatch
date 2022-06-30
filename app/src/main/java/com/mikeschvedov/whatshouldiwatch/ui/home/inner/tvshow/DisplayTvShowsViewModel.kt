package com.mikeschvedov.whatshouldiwatch.ui.home.inner.tvshow

import androidx.lifecycle.*
import com.mikeschvedov.whatshouldiwatch.models.response.*
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.data.repository.ContentMediator
import com.mikeschvedov.whatshouldiwatch.data.repository.MediaRepository
import com.mikeschvedov.whatshouldiwatch.models.adapters.MediaGroupList
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.ParentAdapter
import com.mikeschvedov.whatshouldiwatch.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DisplayTvShowsViewModel @Inject constructor(private val contentMediator: ContentMediator) :
    ViewModel() {

    @Inject
    lateinit var mediaRepository: MediaRepository
    @Inject
    lateinit var remoteApi: RemoteApi

    private val parentAdapter = ParentAdapter()

    private val _clicked = MutableSharedFlow<TmdbItem?>(replay = 0)
    val clicked: SharedFlow<TmdbItem?> = _clicked

    private var _showsFullMediaList = MutableStateFlow(MediaGroupList())
    val showsFullMediaList = _showsFullMediaList.asStateFlow()

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

    fun fetchTvShowsPageMedia() {
        viewModelScope.launch {
            contentMediator.getTvShowsPageMedia()
                .flowOn(Dispatchers.IO)
                .collect { mediaGroupList ->
                    _showsFullMediaList.value = mediaGroupList
                }
        }
    }

    fun updateDBAndFetchTvShows() {
        viewModelScope.launch {
            contentMediator.updateDatabaseViaApi()
            contentMediator.getTvShowsPageMedia()
                .flowOn(Dispatchers.IO)
                .collect { mediaGroupList ->
                    _showsFullMediaList.value = mediaGroupList
                }
        }
    }

}