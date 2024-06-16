package com.bangkit.factha.view.activity.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.data.response.EditProfileResponse
import kotlinx.coroutines.launch


class ProfileViewModel(private val repository: MainRepository) : ViewModel() {
    private val _editProfileResult = MutableLiveData<Result<EditProfileResponse>>()
    val editProfileResult: LiveData<Result<EditProfileResponse>> = _editProfileResult

    fun editProfile(image: String, name: String, email: String, body: String, oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _editProfileResult.value = Result.Loading
            val result = repository.editProfile(image, name, email, body, oldPassword, newPassword)
            _editProfileResult.value = result
        }
    }
}