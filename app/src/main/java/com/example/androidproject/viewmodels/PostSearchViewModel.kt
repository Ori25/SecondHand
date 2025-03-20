package com.example.androidproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidproject.helpers.LoadingState
import com.example.androidproject.models.OtherUser
import com.example.androidproject.ui.utils.LibraryData
import com.example.androidproject.ui.utils.SearchLibraryData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostSearchViewModel
@Inject constructor() : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>(LoadingState.Loaded)
    private val _exceptions = MutableLiveData<Exception?>(null)

    private val userMap = MutableLiveData<Map<String, OtherUser>>()

    private val _searchLiveData = MediatorLiveData<SearchLibraryData>()
    val searchLiveData: LiveData<SearchLibraryData?> = _searchLiveData


    val loadingState: LiveData<LoadingState> get() = _loadingState
    val exceptions: LiveData<Exception?> get() = _exceptions

    private var searchJob: Job? = null


    fun setLibraryData(libraryData: LiveData<LibraryData>) {

        removeLibraryDataSource(libraryData)

        _searchLiveData.addSource(libraryData) {
            _searchLiveData.postValue(SearchLibraryData(it))
            it.allUsers?.associateBy { user -> user.id }?.let { map ->
                userMap.postValue(map)
            }
        }
    }

    fun removeLibraryDataSource(libraryData: LiveData<LibraryData>) {
        _searchLiveData.removeSource(libraryData)
    }


    fun search(query: String) {
        if (query.isBlank()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // wait 500 ms before executing a new request
            val q = query.lowercase()
            val data = _searchLiveData.value ?: return@launch
            data.allPosts = data.allPostsCopy.filter {
                it.title.lowercase().contains(q)
            }
            _searchLiveData.postValue(data)
        }
    }

}