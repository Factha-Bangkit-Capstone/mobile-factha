package com.bangkit.factha.view.activity.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.remote.OcrRepository
import com.bangkit.factha.data.response.OcrResponse
import kotlinx.coroutines.launch

class OcrViewModel(private val repository: OcrRepository) : ViewModel() {
    private val _ocrResult = MutableLiveData<Result<OcrResponse>>()
    val ocrResult: LiveData<Result<OcrResponse>> = _ocrResult

    fun postOcr(imageBase64: String) {
        viewModelScope.launch {
            _ocrResult.value = Result.Loading
            val result = repository.postImageForOcr(imageBase64)
            _ocrResult.value = result
        }
    }
}
