package com.bangkit.factha.data.remote

import android.util.Log
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.network.ApiConfig
import com.bangkit.factha.data.network.ApiServiceAuth
import com.bangkit.factha.data.network.ApiServiceMain
import com.bangkit.factha.data.preference.SettingProfile
import com.bangkit.factha.data.preference.UserDetails
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.response.NewsResponse
import com.bangkit.factha.data.response.ProfileResponse
import com.bangkit.factha.data.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MainRepository(
    private val apiServiceMain: ApiServiceMain,
    private val userPreferences: UserPreferences
) {

    suspend fun getProfile(): Result<ProfileResponse> {
        return try {
            val token = userPreferences.token.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""
            val response = apiServiceMain.getProfile(userId, "Bearer $token")
            if (response.isSuccessful) {
                val profileResponse = response.body()
                if (profileResponse != null) {
                    val userDetails = profileResponse.userData?.firstOrNull()
                    userDetails?.let {
                        val nameProfile = it.name ?: ""
                        val email = it.email ?: ""
                        val password = it.password ?: ""
                        val premium = it.premium ?: 0
                        val imageB64 = it.imageB64 ?: ""
                        userPreferences.saveUserDetails(nameProfile, email, password, premium, imageB64)
                    }
                    Result.Success(profileResponse)
                } else {
                    Result.Error("Empty response body")
                }
            } else {
                Result.Error("Failed to get profile data: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Get profile error: ${e.message}")
        }
    }

    fun getToken(): Flow<String?> {
        return userPreferences.token
    }

    fun getSettingProfile(): Flow<SettingProfile?> {
        return userPreferences.userDetails.map { userDetails ->
            userDetails?.let {
                SettingProfile(it.name ?: "", it.email ?: "", it.imageB64 ?: "")
            } ?: SettingProfile("", "", "")
        }
    }

    fun getUserDetails(): Flow<UserDetails?> {
        return userPreferences.userDetails
    }

    suspend fun logout() {
        userPreferences.clearUserDetails()
    }

    suspend fun getNews(token: String): Result<NewsResponse> {
        return try {
            val token = userPreferences.token.first() ?: ""

            if (token.isNullOrEmpty()) {
                return Result.Error("User token is null or empty")
            }

            val response = withContext(Dispatchers.IO) {
                Log.d("tokenMainRepo", "$token")
                apiServiceMain.getAllNews("Bearer $token")
            }

            Log.d("responsesee", response.toString())

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error("Response body is null")
                }
            } else {
                Result.Error("Failed to fetch news: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }



    companion object {
        @Volatile
        private var instance: MainRepository? = null

        fun getInstance(apiServiceMain: ApiServiceMain, userPreferences: UserPreferences): MainRepository {
            return instance ?: synchronized(this) {
                instance ?: MainRepository(apiServiceMain, userPreferences).also { instance = it }
            }
        }
    }
}