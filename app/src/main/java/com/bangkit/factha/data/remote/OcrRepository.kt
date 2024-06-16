package com.bangkit.factha.data.remote

import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.network.ApiServiceOcr
import com.bangkit.factha.data.response.OcrRequest
import com.bangkit.factha.data.response.OcrResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OcrRepository(
    private val apiServiceOcr: ApiServiceOcr
) {
    suspend fun postImageForOcr(imageBase64: String): Result<OcrResponse> {

        val ocrRequest = OcrRequest(
            image = imageBase64
        )

        return withContext(Dispatchers.IO) {
            try {
                val response = apiServiceOcr.postOCR(ocrRequest)
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    if (newsResponse != null) {
                        Result.Success(newsResponse)
                    } else {
                        Result.Error("Empty response body")
                    }
                } else {
                    Result.Error("Failed to OCR ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Result.Error("Exception occurred: ${e.message}")
            }
        }
    }
}