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
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.network.ApiConfig
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.databinding.FragmentSaveBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.adapter.HomeAdapter
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
    private val bookmarkViewModel by viewModels<BookmarkViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            val userPreferences = UserPreferences.getInstance(requireContext().dataStore)
            val token = userPreferences.token.first()
            withContext(Dispatchers.Main) {
                if (token != null) {
                    val apiService = ApiConfig.getMainService(token)
                    repository = MainRepository.getInstance(apiService, userPreferences)

                    setupRecyclerView()
                    setupViewModel()
                    observeBookmarkedNews()
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireContext())).get(
            BookmarkViewModel::class.java)
    }

    private fun setupRecyclerView() {
        val userPreferences = UserPreferences.getInstance(requireContext().dataStore)

        CoroutineScope(Dispatchers.IO).launch {
            userId = userPreferences.userId.first()
            userId?.let {
                withContext(Dispatchers.Main) {
                    binding.rvSavedNews.layoutManager = LinearLayoutManager(requireContext())
                    homeAdapter = HomeAdapter(emptyList(), userId!!, repository, bookmarkViewModel, viewLifecycleOwner) // Modify as per your adapter setup
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
                    homeAdapter.updateData(bookmarkedNewsList)
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
