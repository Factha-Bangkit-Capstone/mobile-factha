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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.ArrayList

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

            val hoaxScoreModel = newsDetails?.hoaxScore?.toString()?.toFloatOrNull() ?: 0.0f
            val validScoreModel = newsDetails?.validScore?.toString()?.toFloatOrNull() ?: 0.0f

            if (hoaxScoreModel != 0.0f && validScoreModel != 0.0f) {
                val pieChart = binding.piechart
                val entries = ArrayList<PieEntry>()
                entries.add(PieEntry(hoaxScoreModel, "HOAKS"))
                entries.add(PieEntry(validScoreModel, "FAKTA"))

                val pieDataSet = PieDataSet(entries, "| KETERANGAN")
                pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

                val pieData = PieData(pieDataSet)
                pieChart.data = pieData

                pieChart.description.isEnabled = false
                pieChart.animateY(1000)
                pieChart.invalidate()
            } else {
                Log.e("DetailArticleActivity", "Invalid hoaxScoreModel or validScoreModel")
            }

            val hoaxScore = newsDetails?.hoax?.toString()?.toDoubleOrNull() ?: 0.0
            if (hoaxScore == 1.0) {
                binding.tvPredictionResult.text = getString(R.string.hasil_hoaks)
                binding.tvPredictionResult.setTextColor(ContextCompat.getColor(this, R.color.orange_prediction))
            } else {
                binding.tvPredictionResult.text = getString(R.string.hasil_fakta)
                binding.tvPredictionResult.setTextColor(ContextCompat.getColor(this, R.color.green_prediction))
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