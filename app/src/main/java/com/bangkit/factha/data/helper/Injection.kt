package com.bangkit.factha.data.helper

import android.content.Context
import com.bangkit.factha.data.network.ApiConfig
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.data.remote.AuthRepository
import com.bangkit.factha.data.remote.MainRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object InjectionAuth {
    fun provideRepository(context: Context): AuthRepository {
        val dataStore = context.dataStore
        val userPreferences = UserPreferences.getInstance(dataStore)
        val apiServiceAuth = ApiConfig.getAuthService()
        return AuthRepository.getInstance(apiServiceAuth, userPreferences)
    }
}

object InjectionMain {
    fun provideRepository(context: Context): MainRepository {
        val dataStore = context.dataStore
        val userPreferences = UserPreferences.getInstance(dataStore)
        val token = runBlocking { userPreferences.token.first() }
        val apiServiceMain = ApiConfig.getMainService(token ?: "")
        return MainRepository.getInstance(apiServiceMain, userPreferences)
    }
}