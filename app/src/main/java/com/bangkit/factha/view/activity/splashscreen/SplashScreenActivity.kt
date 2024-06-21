package com.bangkit.factha.view.activity.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bangkit.factha.R
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.activity.MainActivity
import com.bangkit.factha.view.activity.auth.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var buttonAuth: Button

    private val viewModel by viewModels<SplashScreenViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Thread.sleep(1_500)
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        setupView()
        checkToken()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        buttonAuth = findViewById(R.id.btn_Auth)
        buttonAuth.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun checkToken() {
        viewModel.getToken().observe(this) { token ->
            if (token != null) {
                navigateToMain()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
