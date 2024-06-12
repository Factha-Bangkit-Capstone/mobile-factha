package com.bangkit.factha.view.activity.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.factha.data.remote.AuthRepository
import com.bangkit.factha.data.response.LoginResponse
import com.bangkit.factha.data.helper.Result
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            val result = repository.login(email, password)
            _loginResult.value = result
        }
    }
}