package com.bangkit.factha.view.fragment.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.factha.R
import com.bangkit.factha.databinding.FragmentHomeBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.activity.MainViewModel
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.view.activity.article.AddArticleActivity
import com.bangkit.factha.view.activity.settings.AboutActivity
import com.bangkit.factha.view.adapter.HomeAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeAdapter: HomeAdapter

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = UserPreferences.getInstance(requireContext().dataStore)
        val token = runBlocking { userPreferences.token.first() }
        if (token != null) {
            observeNews()
        }

        setupRecyclerView()
        binding.btnAddArticle.setOnClickListener { addNews() }
        binding.btnWrite.setOnClickListener { addNews() }
        binding.btnWriteIcon.setOnClickListener { addNews() }
    }

    private fun setupRecyclerView() {
        binding.rvSelectedForYou.layoutManager = LinearLayoutManager(requireContext())
        homeAdapter = HomeAdapter(emptyList())
        binding.rvSelectedForYou.adapter = homeAdapter
    }

    private fun observeNews() {
        viewModel.news.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.loadingMenuSelectedForYou.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.loadingMenuSelectedForYou.visibility = View.GONE
                    val newsData = result.data.newsData ?: emptyList()
                    homeAdapter = HomeAdapter(newsData)
                    binding.rvSelectedForYou.adapter = homeAdapter
                }
                is Result.Error -> {
                    Log.e("Result News Error", "Error: ${result.error}")
                }
                else -> { Log.e("Observe News Error", "Observe News Error") }
            }
        }
        viewModel.getNews()
    }

    private fun addNews(){
        val intent = Intent(requireContext(), AddArticleActivity::class.java)
        startActivity(intent)
    }
}