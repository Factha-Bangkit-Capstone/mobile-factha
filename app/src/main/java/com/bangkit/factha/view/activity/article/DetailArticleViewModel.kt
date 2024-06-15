package com.bangkit.factha.view.activity.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.data.response.NewsDataItem
import kotlinx.coroutines.launch

class DetailArticleViewModel(private val repository: MainRepository) : ViewModel() {
    private val _newsDetails = MutableLiveData<NewsDataItem?>()
    val newsDetails: LiveData<NewsDataItem?> get() = _newsDetails

    fun fetchNewsDetails(newsId: String) {
        viewModelScope.launch {
            val result = repository.getNewsDetail(newsId)
            if (result is Result.Success) {
                _newsDetails.value = result.data
            } else {
                // Handle error
                _newsDetails.value = null
            }
        }
    }
}