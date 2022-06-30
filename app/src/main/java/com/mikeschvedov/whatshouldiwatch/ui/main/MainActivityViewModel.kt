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

    /* --- LiveData --- */
    // Exception Error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

}

