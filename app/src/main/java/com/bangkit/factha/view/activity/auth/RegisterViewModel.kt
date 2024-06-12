package com.bangkit.factha.view.activity.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.factha.data.remote.AuthRepository
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = Result.Loading
            val result = repository.register(name, email, password)
            _registerResult.value = result
        }
    }
}