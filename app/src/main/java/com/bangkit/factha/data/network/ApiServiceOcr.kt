package com.bangkit.factha.data.network

import com.bangkit.factha.data.response.AddNewsRequest
import com.bangkit.factha.data.response.OcrRequest
import com.bangkit.factha.data.response.OcrResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiServiceOcr {

    @POST("process_image")
    suspend fun postOCR(
        @Body requestBody: OcrRequest
    ): Response<OcrResponse>
}