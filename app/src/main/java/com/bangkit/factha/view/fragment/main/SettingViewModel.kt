package com.bangkit.factha.view.fragment.main

import androidx.lifecycle.ViewModel
import com.bangkit.factha.data.remote.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: MainRepository): ViewModel() {
    fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.logout()
        }
    }
}