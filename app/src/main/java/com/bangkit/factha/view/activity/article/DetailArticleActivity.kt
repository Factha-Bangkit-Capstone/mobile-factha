package com.bangkit.factha.view.activity.article

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.factha.R
import com.bangkit.factha.databinding.ActivityDetailArticleBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bumptech.glide.Glide

class DetailArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailArticleBinding
    private lateinit var viewModel: DetailArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsId = intent.getStringExtra("newsId") ?: throw IllegalArgumentException("News ID not provided")

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(applicationContext)).get(DetailArticleViewModel::class.java)
        viewModel.fetchNewsDetails(newsId)
        binding.btnBack.setOnClickListener { finish() }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.newsDetails.observe(this, Observer { newsDetails ->
            binding.tvTitleArticle.text = newsDetails?.title
            binding.tvNewsBody.text = newsDetails?.body

            val hoaxScore = newsDetails?.hoax?.toString()?.toDoubleOrNull() ?: 0.0
            if (hoaxScore == 1.0) {
                binding.tvPredictionResult.text = "HOAKS"
                binding.tvPredictionResult.setTextColor(ContextCompat.getColor(this, R.color.red))
            } else {
                binding.tvPredictionResult.text = "FAKTA"
                binding.tvPredictionResult.setTextColor(ContextCompat.getColor(this, R.color.green))
            }

            val imageBytes = Base64.decode(newsDetails?.imageB64, Base64.DEFAULT)
            with(binding) {
                Glide.with(this@DetailArticleActivity)
                    .load(imageBytes)
                    .into(ivImageArticle)

            }
        })
    }

}