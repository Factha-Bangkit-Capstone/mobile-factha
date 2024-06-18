package com.bangkit.factha.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bangkit.factha.R
import com.bangkit.factha.databinding.ActivityMainBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.activity.splashscreen.SplashScreenActivity
import com.bangkit.factha.view.fragment.main.HomeFragment
import com.bangkit.factha.view.fragment.main.SaveFragment
import com.bangkit.factha.view.fragment.main.SettingFragment
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.response.ProfileResponse
import com.bangkit.factha.view.fragment.main.ArticleFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        viewModel.getToken().observe(this) { token ->
            if(token==null){
                startActivity(Intent(this, SplashScreenActivity::class.java))
                finish()
            }

            if (token != null) {
                binding = ActivityMainBinding.inflate(layoutInflater)
                val view = binding.root
                setContentView(view)
                replaceFragment(HomeFragment())

                binding.btnNav.setOnNavigationItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.home -> {
                            replaceFragment(HomeFragment())
                            true
                        }
                        R.id.article -> {
                            replaceFragment(ArticleFragment())
                            true
                        }
                        R.id.save -> {
                            replaceFragment(SaveFragment())
                            true
                        }
                        R.id.setting -> {
                            replaceFragment(SettingFragment())
                            true
                        }
                        else -> false
                    }
                }
                viewModel.getProfile()
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
