package com.bangkit.factha.view.fragment.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.data.response.NewsDataItem
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

    private fun getSavedNews() {
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

    private fun fetchSavedNews() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.saveSavedNewsDirect()) {
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

}
