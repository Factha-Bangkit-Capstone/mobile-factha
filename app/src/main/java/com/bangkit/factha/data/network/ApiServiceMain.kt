package com.bangkit.factha.data.network

import com.bangkit.factha.data.response.AddNewsRequest
import com.bangkit.factha.data.response.DeleteSavedNewsResponse
import com.bangkit.factha.data.response.EditProfileResponse
import com.bangkit.factha.data.response.NewsResponse
import com.bangkit.factha.data.response.ProfileResponse
import com.bangkit.factha.data.response.RegisterResponse
import com.bangkit.factha.data.response.SaveNewsRequest
import com.bangkit.factha.data.response.SavedDataItem
import com.bangkit.factha.data.response.SavedNewsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
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
        @Header("X-Auth-Token") token: String,
    ): Response<NewsResponse>

    @GET("news/{newsId}")
    suspend fun getNewsDetail(
        @Header("X-Auth-Token") token: String,
        @Path("newsId") newsId: String
    ): Response<NewsResponse>

    @POST("news")
    suspend fun addNews(
        @Header("X-Auth-Token") token: String,
        @Body requestBody: AddNewsRequest
    ): Response<NewsResponse>

    @POST("savedNews")
    suspend fun saveNews(
        @Header("X-Auth-Token") token: String,
        @Body requestBody: SaveNewsRequest
    ): Response<SavedNewsResponse>

    @GET("savedNews/{userId}")
    suspend fun getSavedNews(
        @Header("X-Auth-Token") token: String,
        @Path("userId") userId: String
    ): Response<SavedNewsResponse>

    @DELETE("savedNews/{savedNewsId}")
    suspend fun deleteSavedNews(
        @Header("X-Auth-Token") token: String,
        @Path("savedNewsId") savedNewsId: String
    ): Response<Unit>

    @FormUrlEncoded
    @PUT("users/{userId}")
    suspend fun editProfile(
        @Path("userId") userId: String,
        @Header("X-Auth-Token") token: String,
        @Field("image") image: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("body") body: String,
        @Field("oldPassword") oldPassword: String,
        @Field("newPassword") newPassword: String
    ): Response<EditProfileResponse>
}