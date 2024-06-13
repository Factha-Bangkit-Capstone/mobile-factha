package com.bangkit.factha.view.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.preference.UserDetails
import com.bangkit.factha.data.response.NewsResponse
import com.bangkit.factha.data.response.ProfileResponse
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    private val _profileData = MutableLiveData<Result<ProfileResponse>>()
    val profileData: LiveData<Result<ProfileResponse>> = _profileData

    private val _news = MutableLiveData<Result<NewsResponse>>()
    val news: LiveData<Result<NewsResponse>> get() = _news


    fun getProfile() {
        viewModelScope.launch {
            _profileData.value = repository.getProfile()
        }
    }

    fun getUserDetails(): LiveData<UserDetails?> {
        return repository.getUserDetails().asLiveData()
    }

    fun getNews() {
        viewModelScope.launch {
            val result = repository.getNews()
            _news.postValue(result)
        }
    }

    fun getToken(): LiveData<String?> {
        return repository.getToken().asLiveData()
    }
}