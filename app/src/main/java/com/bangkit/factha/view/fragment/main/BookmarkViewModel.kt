package com.bangkit.factha.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.data.response.NewsDataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarkViewModel(private val repository: MainRepository) : ViewModel() {

    private val _bookmarkedNews = MutableLiveData<Result<List<NewsDataItem>>>()
    val bookmarkedNews: LiveData<Result<List<NewsDataItem>>>
        get() = _bookmarkedNews

    init {
        getSavedNews()
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
