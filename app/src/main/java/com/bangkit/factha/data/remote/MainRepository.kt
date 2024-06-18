package com.bangkit.factha.data.remote

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.network.ApiConfig
import com.bangkit.factha.data.network.ApiServiceAuth
import com.bangkit.factha.data.network.ApiServiceMain
import com.bangkit.factha.data.preference.SavedNews
import com.bangkit.factha.data.preference.SettingProfile
import com.bangkit.factha.data.preference.UserDetails
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.data.response.AddNewsRequest
import com.bangkit.factha.data.response.EditProfileResponse
import com.bangkit.factha.data.response.NewsDataItem
import com.bangkit.factha.data.response.NewsResponse
import com.bangkit.factha.data.response.ProfileResponse
import com.bangkit.factha.data.response.RegisterResponse
import com.bangkit.factha.data.response.SaveNewsRequest
import com.bangkit.factha.data.response.SavedDataItem
import com.bangkit.factha.data.response.SavedNewsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository(
    private val apiServiceMain: ApiServiceMain,
    val userPreferences: UserPreferences
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

    suspend fun getNews(): Result<NewsResponse> {
        return try {
            val token = userPreferences.token.first() ?: ""

            val response = apiServiceMain.getAllNews("Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error("Response body is null")
                }
            } else {
                Result.Error("Failed to fetch saved news: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }

    suspend fun editProfile(image: String, name: String, email: String, body: String, oldPassword: String,  newPassword: String): Result<EditProfileResponse> {
        return try {
            val token = userPreferences.token.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""
            val response = apiServiceMain.editProfile(userId,"Bearer $token", image, name, email, body, oldPassword, newPassword)
            if (response.isSuccessful) {
                val registerResponse = response.body()
                if (registerResponse != null) {
                    Result.Success(registerResponse)
                } else {
                    Result.Error("Empty response body")
                }
            } else {
                Result.Error("Failed to Edit Profile: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("Edit Profile", "Edit Profile Error", e)
            Result.Error("Edit Profile Error: ${e.message}")
        }
    }


    suspend fun getNewsDetail(newsId: String): Result<NewsDataItem?> {
        return try {
            val token = userPreferences.token.first() ?: ""
            val response = apiServiceMain.getNewsDetail("Bearer $token", newsId)

            if (response.isSuccessful) {
                val newsResponse = response.body()

                val newsDataItem = newsResponse?.newsData?.find { it?.newsId == newsId }

                if (newsDataItem != null) {
                    Result.Success(newsDataItem)
                } else {
                    Result.Error("News item not found")
                }
            } else {
                Result.Error("Failed to fetch saved news: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }

    suspend fun postNews(
        token: String,
        userId: String,
        title: String,
        tags: String,
        body: String,
        imageBase64: String
    ): Result<NewsResponse> {
        val newsRequest = AddNewsRequest(
            userId = userId,
            title = title,
            tags = tags,
            body = body,
            image = imageBase64
        )

        return withContext(Dispatchers.IO) {
            try {
                val response = apiServiceMain.addNews("Bearer $token", newsRequest)
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    if (newsResponse != null) {
                        Result.Success(newsResponse)
                    } else {
                        Result.Error("Empty response body")
                    }
                } else {
                    Result.Error("Failed to fetch saved news: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Result.Error("Exception occurred: ${e.message}")
            }
        }
    }

    suspend fun toggleBookmark(newsId: String): Result<Boolean> {
        return try {
            val isBookmarkedResult = isNewsBookmarked(userPreferences.userId.firstOrNull() ?: "", newsId)

            if (isBookmarkedResult is Result.Success) {
                val isCurrentlyBookmarked = isBookmarkedResult.data

                if (isCurrentlyBookmarked) {
                    val removeBookmarkResult = removeBookmark(newsId)
                    if (removeBookmarkResult is Result.Success && removeBookmarkResult.data) {
                        Result.Success(false)
                    } else {
                        Result.Error("Failed to remove bookmark")
                    }
                } else {
                    val saveNewsResult = saveNews(userPreferences.userId.firstOrNull() ?: "", newsId)
                    if (saveNewsResult is Result.Success) {
                        Result.Success(true)
                    } else {
                        Result.Error("Failed to save news as bookmark")
                    }
                }
            } else {
                Result.Error("Failed to check bookmark status")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }

    private suspend fun isNewsBookmarked(userId: String, newsId: String): Result<Boolean> {
        return try {
            val token = userPreferences.token.first() ?: ""
            val response = apiServiceMain.getSavedNews("Bearer $token", userId)

            if (response.isSuccessful) {
                val savedNewsList = response.body()?.data ?: emptyList()
                val isBookmarked = savedNewsList.any { it?.newsId == newsId }
                Result.Success(isBookmarked)
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.e("MainRepository", "Failed to fetch saved news: ${response.code()}, $errorBody")
                Result.Error("Failed to fetch saved news: ${response.code()} ($errorBody)")
            }
        } catch (e: Exception) {
            Log.e("MainRepository", "Exception occurred: ${e.message}")
            Result.Error("Error occurred: ${e.message}")
        }
    }

    private suspend fun saveNews(userId: String, newsId: String): Result<SavedNewsResponse?> {
        return try {
            val token = userPreferences.token.first() ?: ""
            val saveNewsRequest = SaveNewsRequest(userId, newsId)
            val response = apiServiceMain.saveNews("Bearer $token", saveNewsRequest)

            if (response.isSuccessful) {
                Result.Success(response.body())
            } else {
                Result.Error("Failed to save news: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }

    private suspend fun removeBookmark(newsId: String): Result<Boolean> {
        return try {
            val token = userPreferences.token.firstOrNull() ?: ""
            if (token.isEmpty()) {
                return Result.Error("Token is null or empty")
            }

            val savedNewsResponse = apiServiceMain.getSavedNews("Bearer $token", userPreferences.userId.firstOrNull() ?: "")
            if (!savedNewsResponse.isSuccessful) {
                val errorBody = savedNewsResponse.errorBody()?.string() ?: ""
                Log.e("MainRepository", "Failed to fetch saved news: ${savedNewsResponse.code()}, $errorBody")
                return Result.Error("Failed to fetch saved news: ${savedNewsResponse.code()} ($errorBody)")
            }

            val savedNewsList = savedNewsResponse.body()?.data ?: emptyList()
            val savedItem = savedNewsList.find { it?.newsId == newsId }
            if (savedItem?.id == null) {
                Log.e("MainRepository", "Saved news item not found for newsId: $newsId")
                return Result.Error("Saved news item not found for newsId: $newsId")
            }

            val response = apiServiceMain.deleteSavedNews("Bearer $token", savedItem.id)

            if (response.isSuccessful) {
                Log.d("MainRepository", "Successfully removed bookmark for id: ${savedItem.id}")
                Result.Success(true)
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.e("MainRepository", "Failed to remove bookmark: ${response.code()}, $errorBody")
                Result.Error("Failed to remove bookmark: ${response.code()} ($errorBody)")
            }
        } catch (e: Exception) {
            Log.e("MainRepository", "Exception occurred: ${e.message}")
            Result.Error("Error occurred: ${e.message}")
        }
    }

    suspend fun getSavedNews(): Result<SavedNewsResponse> {
        return try {
            val token = userPreferences.token.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            val response = apiServiceMain.getSavedNews("Bearer $token", userId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error("Response body is null")
                }
            } else {
                Result.Error("Failed to get saved news: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }

    suspend fun saveSavedNewsDirect(): Result<SavedNewsResponse> {
        return try {
            val token = userPreferences.token.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""
            Log.d("useridsrepo", "$userId")
            val response = apiServiceMain.getSavedNews("Bearer $token", userId)

            if (response.isSuccessful) {
                val savedNewsResponse = response.body()
                if (savedNewsResponse != null) {
                    val userDetails = savedNewsResponse.data?.firstOrNull()
                    userDetails?.let {
                        val id = it.id ?: ""
                        val newsId = it.newsId ?: ""
                        userPreferences.saveSavedNews(id, newsId) // Save to data store
                        Log.d("saved bro saved", "$newsId")
                    }
                    Result.Success(savedNewsResponse)
                } else {
                    Result.Error("Empty response body")
                }
            } else {
                Result.Error("Failed to get profile data: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Get saved news error: ${e.message}")
        }
    }


    suspend fun deleteSavedNews() {
        userPreferences.clearSavedNews()
    }

    suspend fun searchNews(keyword: String): Result<NewsResponse> {
        return try {
            val token = userPreferences.token.first() ?: ""
            val response = apiServiceMain.searchNews("Bearer $token", keyword)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error("Response body is null")
                }
            } else {
                Result.Error("Failed to find news: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Find News error: ${e.message}")
        }
    }

/*    suspend fun searchUser(keyword: String) {
        val token = userPreferences.token.first() ?: ""
        val response = apiServiceMain.searchNews("Bearer $token", keyword)

        response.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(
                call: Call<NewsResponse>,
                response: Response<NewsResponse>
            ) {
                if (response.isSuccessful) {
                    _listNews.value = response.body()?.newsData as List<NewsDataItem>
                } else {
                    Log.e("Fail", "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.e("APIRequest", "onFailure: ${t.message}")

                t.printStackTrace()
            }
        })
    }*/

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