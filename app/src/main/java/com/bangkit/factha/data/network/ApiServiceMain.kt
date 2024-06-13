package com.bangkit.factha.data.network

import com.bangkit.factha.data.response.NewsResponse
import com.bangkit.factha.data.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceMain {
    @GET("users/{userId}")
    suspend fun getProfile(
        @Path("userId") userId: String,
        @Header("X-Auth-Token") token: String
    ): Response<ProfileResponse>

    @GET("news")
    suspend fun getAllNews(
        @Header("Authorization") token: String,
    ): Response<NewsResponse>

    @GET("news/{newsId}")
    suspend fun getNewsDetail(
        @Header("X-Auth-Token") token: String,
        @Path("newsId") newsId: String
    ): Response<NewsResponse>
}