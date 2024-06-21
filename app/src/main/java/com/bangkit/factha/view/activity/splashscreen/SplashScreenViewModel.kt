package com.bangkit.factha.view.activity.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bangkit.factha.data.remote.MainRepository

class SplashScreenViewModel(private val repository: MainRepository) : ViewModel() {

    fun getToken(): LiveData<String?> {
        return repository.getToken().asLiveData()
    }
}
