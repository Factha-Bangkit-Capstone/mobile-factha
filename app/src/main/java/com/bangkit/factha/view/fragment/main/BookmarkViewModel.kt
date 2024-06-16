package com.bangkit.factha.view.fragment.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.preference.SavedNews
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.data.response.NewsDataItem
import com.bangkit.factha.data.response.ProfileResponse
import com.bangkit.factha.data.response.SavedNewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkViewModel(private val repository: MainRepository) : ViewModel() {

    private val _bookmarkedNews = MutableLiveData<Result<List<NewsDataItem>>>()
    val bookmarkedNews: LiveData<Result<List<NewsDataItem>>>
        get() = _bookmarkedNews

    private val _savedNewsList = MutableLiveData<List<String>>()
    val savedNewsList: LiveData<List<String>> = _savedNewsList

    init {
        getSavedNews()
        fetchSavedNews()
    }

    fun getSavedNews() {
        viewModelScope.launch(Dispatchers.IO) {
            _bookmarkedNews.postValue(Result.Loading)
            val result = repository.getSavedNews()
            if (result is Result.Success) {
                val savedDataItems = result.data.data ?: emptyList()
                val newsDataItems = savedDataItems.mapNotNull { savedItem ->
                    savedItem?.newsId?.let { newsId ->
                        repository.getNewsDetail(newsId).let { newsDetailResult ->
                            if (newsDetailResult is Result.Success) {
                                newsDetailResult.data
                            } else {
                                null
                            }
                        }
                    }
                }
                _bookmarkedNews.postValue(Result.Success(newsDataItems))
            } else if (result is Result.Error) {
                _bookmarkedNews.postValue(Result.Error(result.error))
            }
        }
    }

    fun fetchSavedNews() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.saveSavedNewsDirect()

            when (result) {
                is Result.Success -> {
                    val newsIds = result.data.data?.mapNotNull { it?.newsId } ?: emptyList()

                    withContext(Dispatchers.Main) {
                        _savedNewsList.value = newsIds
                    }
                    Log.d("fetchSavedNews", "Fetched news IDs: $newsIds")
                }
                is Result.Error -> {
                    Log.e("fetchSavedNews", "Error fetching saved news: ${result.error}")
                }
                Result.Loading -> {
                    Log.d("fetchSavedNews", "Loading saved news...")
                }
            }
        }
    }

    val savedNews: LiveData<SavedNews?> = repository.userPreferences.savedNews.asLiveData()

    suspend fun removeSavedNewsDirect() {
        repository.deleteSavedNews()
    }

    fun toggleBookmark(newsId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.toggleBookmark(newsId)) {
                is Result.Success -> {
                    getSavedNews()
                }
                is Result.Error -> {
                    _bookmarkedNews.postValue(Result.Error(result.error))
                }
                Result.Loading -> TODO()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Perform any necessary cleanup operations
    }
}
