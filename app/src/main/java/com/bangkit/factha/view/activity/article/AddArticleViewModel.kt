package com.bangkit.factha.view.activity.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.data.response.NewsResponse
import kotlinx.coroutines.launch
import com.bangkit.factha.data.helper.Result

class AddArticleViewModel(private val repository: MainRepository) : ViewModel() {

    private val _postNewsResult = MutableLiveData<Result<NewsResponse>>()
    val postNewsResult: LiveData<Result<NewsResponse>> = _postNewsResult

    fun postNews(token: String, userId: String, title: String, tags: String, body: String, imageBase64: String) {
        viewModelScope.launch {
            _postNewsResult.value = Result.Loading
            val result = repository.postNews(token, userId, title, tags, body, imageBase64)
            _postNewsResult.value = result
        }
    }
}