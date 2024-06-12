package com.bangkit.factha.data.remote

import com.bangkit.factha.data.network.ApiServiceAuth
import com.bangkit.factha.data.network.ApiServiceMain
import com.bangkit.factha.data.preference.UserPreferences
import kotlinx.coroutines.flow.Flow

class MainRepository(
    private val apiServiceMain: ApiServiceMain,
    private val userPreferences: UserPreferences
) {

    fun getToken(): Flow<String?> {
        return userPreferences.token
    }

    suspend fun logout() {
        userPreferences.clearToken()
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