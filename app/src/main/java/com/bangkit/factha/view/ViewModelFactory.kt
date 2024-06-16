package com.bangkit.factha.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.factha.data.helper.InjectionAuth
import com.bangkit.factha.data.helper.InjectionMain
import com.bangkit.factha.data.network.ApiConfigOcr
import com.bangkit.factha.data.remote.OcrRepository
import com.bangkit.factha.view.activity.MainViewModel
import com.bangkit.factha.view.activity.article.AddArticleViewModel
import com.bangkit.factha.view.activity.article.DetailArticleViewModel
import com.bangkit.factha.view.activity.article.OcrViewModel
import com.bangkit.factha.view.activity.auth.LoginViewModel
import com.bangkit.factha.view.activity.auth.RegisterViewModel
import com.bangkit.factha.view.activity.settings.ProfileViewModel
import com.bangkit.factha.view.fragment.main.SettingViewModel
import com.bangkit.factha.viewmodels.BookmarkViewModel

class ViewModelFactory private constructor(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                val repository = InjectionAuth.provideRepository(context)
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                val repository = InjectionAuth.provideRepository(context)
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                SettingViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddArticleViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                AddArticleViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailArticleViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                DetailArticleViewModel(repository) as T
            }
            modelClass.isAssignableFrom(BookmarkViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                BookmarkViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(OcrViewModel::class.java) -> {
                OcrViewModel(OcrRepository(ApiConfigOcr.getOcrService())) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(context).also { instance = it }
            }
    }
}
