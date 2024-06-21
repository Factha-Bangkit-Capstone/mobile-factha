package com.bangkit.factha.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bangkit.factha.R
import com.bangkit.factha.databinding.ActivityMainBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.fragment.main.ArticleFragment
import com.bangkit.factha.view.fragment.main.HomeFragment
import com.bangkit.factha.view.fragment.main.SaveFragment
import com.bangkit.factha.view.fragment.main.SettingFragment

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        replaceFragment(HomeFragment())

        binding.btnNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    if (!isCurrentFragment(HomeFragment::class.java)) {
                        replaceFragment(HomeFragment())
                    }
                    true
                }
                R.id.article -> {
                    if (!isCurrentFragment(ArticleFragment::class.java)) {
                        replaceFragment(ArticleFragment())
                    }
                    true
                }
                R.id.save -> {
                    if (!isCurrentFragment(SaveFragment::class.java)) {
                        replaceFragment(SaveFragment())
                    }
                    true
                }
                R.id.setting -> {
                    if (!isCurrentFragment(SettingFragment::class.java)) {
                        replaceFragment(SettingFragment())
                    }
                    true
                }
                else -> false
            }
        }
        viewModel.getProfile()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun isCurrentFragment(fragmentClass: Class<out Fragment>): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        return currentFragment != null && currentFragment::class.java == fragmentClass
    }
}
