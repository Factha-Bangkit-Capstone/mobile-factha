package com.bangkit.factha.view.fragment.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bangkit.factha.data.preference.SettingProfile
import com.bangkit.factha.data.remote.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: MainRepository): ViewModel() {

    fun getSettingProfile(): LiveData<SettingProfile?> {
        return repository.getSettingProfile().asLiveData()
    }
    fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.logout()
        }
    }
}