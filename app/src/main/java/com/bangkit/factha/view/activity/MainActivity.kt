package com.bangkit.factha.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.factha.databinding.ActivityMainBinding
import com.bangkit.factha.view.fragment.main.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.fragmentContainer.let {
            supportFragmentManager.beginTransaction()
                .replace(it.id, HomeFragment())
                .commit()
        }
    }
}
