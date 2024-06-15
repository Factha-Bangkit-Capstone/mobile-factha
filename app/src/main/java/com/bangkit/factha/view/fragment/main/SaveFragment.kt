package com.bangkit.factha.view.fragment.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.factha.R
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.network.ApiConfig
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.databinding.FragmentHomeBinding
import com.bangkit.factha.databinding.FragmentSaveBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.activity.MainViewModel
import com.bangkit.factha.view.adapter.ArticleAdapter
import com.bangkit.factha.view.adapter.HomeAdapter
import com.bangkit.factha.viewmodels.BookmarkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SaveFragment : Fragment() {

    private var _binding: FragmentSaveBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var viewModel: BookmarkViewModel
    private lateinit var repository: MainRepository
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userPreferences = UserPreferences.getInstance(requireContext().dataStore)
        val token = runBlocking { userPreferences.token.first() }

        if (token != null) {
            val apiService = ApiConfig.getMainService(token)
            repository = MainRepository.getInstance(apiService, userPreferences)

            setupViewModel()
            observeBookmarkedNews()
            setupRecyclerView()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireContext())).get(BookmarkViewModel::class.java)
    }

    private fun setupRecyclerView() {
        val userPreferences = UserPreferences.getInstance(requireContext().dataStore)

        CoroutineScope(Dispatchers.IO).launch {
            userId = userPreferences.userId.first()
            userId?.let {
                withContext(Dispatchers.Main) {
                    binding.rvSavedNews.layoutManager = LinearLayoutManager(requireContext())
                    homeAdapter = HomeAdapter(emptyList(), userId!!, repository) // Modify as per your adapter setup
                    binding.rvSavedNews.adapter = homeAdapter
                }
            }
        }
    }

    private fun observeBookmarkedNews() {
        viewModel.bookmarkedNews.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.loadingMenuSaved.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.loadingMenuSaved.visibility = View.GONE
                    val bookmarkedNewsList = result.data
                    homeAdapter.updateData(bookmarkedNewsList) // Update adapter data
                }
                is Result.Error -> {
                    // Handle error scenario
                    Log.e("SaveFragment", "Failed to fetch bookmarked news: ${result.error}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
