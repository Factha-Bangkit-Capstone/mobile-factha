package com.bangkit.factha.data.helper

import android.content.Context
import com.bangkit.factha.data.network.ApiConfig
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.data.remote.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object InjectionAuth {
    fun provideRepository(context: Context): AuthRepository {
        val dataStore = context.dataStore
        val userPreferences = UserPreferences.getInstance(dataStore)
        val token = runBlocking { userPreferences.token.first() }
        val apiService = ApiConfig.getAuthService(token ?: "")
        return AuthRepository.getInstance(apiService, userPreferences)
    }
}