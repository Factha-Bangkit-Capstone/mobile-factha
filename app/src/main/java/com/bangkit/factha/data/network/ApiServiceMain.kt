package com.bangkit.factha.data.network

import com.bangkit.factha.data.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiServiceMain {
    @GET("users/{userId}")
    suspend fun getProfile(
        @Path("userId") userId: String,
        @Header("X-Auth-Token") token: String
    ): Response<ProfileResponse>
}