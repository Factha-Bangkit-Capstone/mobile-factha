package com.bangkit.factha.data.remote

import android.util.Log
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.network.ApiServiceAuth
import com.bangkit.factha.data.network.ApiServiceMain
import com.bangkit.factha.data.preference.UserDetails
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.response.ProfileResponse
import com.bangkit.factha.data.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MainRepository(
    private val apiServiceMain: ApiServiceMain,
    private val userPreferences: UserPreferences
) {

    suspend fun getProfile(name: String): Result<ProfileResponse> {
        return try {
            val token = userPreferences.token.first() ?: ""
            val userid = userPreferences.userId.first() ?: ""
            val response = apiServiceMain.getProfile(userid, "Bearer $token")
            if (response.isSuccessful) {
                val profileResponse = response.body()
                if (profileResponse != null) {
                    Result.Success(profileResponse)
                } else {
                    Result.Error("Empty response body")
                }
            } else {
                Result.Error("Failed to register: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("Register", "Registration error", e)
            Result.Error("Register error: ${e.message}")
        }
    }



    fun getToken(): Flow<String?> {
        return userPreferences.token
    }

    fun getUserId(): Flow<String?> {
        return userPreferences.userId
    }

    fun getUserDetails(): Flow<UserDetails?> {
        return userPreferences.userDetails
    }

    suspend fun logout() {
        userPreferences.clearUserDetails()
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