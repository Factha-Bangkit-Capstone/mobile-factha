package com.bangkit.factha.view.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bangkit.factha.data.remote.MainRepository

class MainViewModel(private val repository: MainRepository) : ViewModel() {
    fun getToken(): LiveData<String?> {
        return repository.getToken().asLiveData()
    }
}