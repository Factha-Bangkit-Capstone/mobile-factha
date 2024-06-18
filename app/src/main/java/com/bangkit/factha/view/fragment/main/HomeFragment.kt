package com.bangkit.factha.view.fragment.main

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.factha.R
import com.bangkit.factha.databinding.FragmentHomeBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.activity.MainViewModel
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.network.ApiConfig
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.view.activity.article.AddArticleActivity
import com.bangkit.factha.view.activity.settings.AboutActivity
import com.bangkit.factha.view.activity.settings.ProfileActivity
import com.bangkit.factha.view.adapter.HomeAdapter
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var homeAdapter: HomeAdapter? = null
    private lateinit var repository: MainRepository
    private var userId: String? = null
    private val bookmarkViewModel by viewModels<BookmarkViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val viewModelSetting by viewModels<SettingViewModel> {
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
            val apiService = ApiConfig.getMainService(token)
            repository = MainRepository.getInstance(apiService, userPreferences)

            val transition = AnimationUtils.loadAnimation(requireContext(), R.anim.transition_fragment_home)

            binding.imageView4.apply {
                visibility = View.VISIBLE
                startAnimation(transition)
            }
            binding.textView7.apply {
                visibility = View.VISIBLE
                startAnimation(transition)
            }
            binding.textView8.apply {
                visibility = View.VISIBLE
                startAnimation(transition)
            }
            binding.btnAddArticle.apply {
                visibility = View.VISIBLE
                startAnimation(transition)
            }
            binding.view.apply {
                visibility = View.VISIBLE
                startAnimation(transition)
            }
            binding.textView9.apply {
                visibility = View.VISIBLE
                startAnimation(transition)
                }

            setupRecyclerView()
            observeNews()
        }

        viewModelSetting.getSettingProfile().observe(viewLifecycleOwner) { settingProfile ->
            settingProfile?.imageBase64?.let {
                val imageBytes = Base64.decode(settingProfile.imageBase64, Base64.DEFAULT)
                Glide.with(requireContext())
                    .asBitmap()
                    .load(imageBytes)
                    .into(binding.imageView3)
            }
        }

        binding.btnAddArticle.setOnClickListener { addNews() }
        binding.btnWrite.setOnClickListener { addNews() }
        binding.btnWriteIcon.setOnClickListener { addNews() }
        binding.imageView3.setOnClickListener{ selectProfile() }

        bookmarkViewModel.savedNewsList.observe(viewLifecycleOwner) { savedNewsIds ->
            homeAdapter?.updateBookmarkedNews(savedNewsIds)
        }
    }

    private fun selectProfile() {
        val intent = Intent(requireContext(), ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        val userPreferences = UserPreferences.getInstance(requireContext().dataStore)

        CoroutineScope(Dispatchers.IO).launch {
            userId = userPreferences.userId.first()
            userId?.let {
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        binding.rvSelectedForYou.layoutManager = LinearLayoutManager(requireContext())
                        homeAdapter = HomeAdapter(emptyList(), it, repository,bookmarkViewModel ,viewLifecycleOwner)
                        binding.rvSelectedForYou.adapter = homeAdapter
                    }
                }
            }
        }
    }


    private fun observeNews() {
        viewModel.news.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.loadingMenuSelectedForYou.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.loadingMenuSelectedForYou.visibility = View.GONE
                    val newsData = result.data.newsData?.take(3) ?: emptyList()
                    userId?.let {
                        homeAdapter = HomeAdapter(newsData, it, repository, bookmarkViewModel ,viewLifecycleOwner)
                        binding.rvSelectedForYou.adapter = homeAdapter
                    }
                }
                is Result.Error -> {
                    Log.e("TAG", "Observe news failed")
                }
            }
        }
        viewModel.getNews()
    }

    private fun addNews() {
        val intent = Intent(requireContext(), AddArticleActivity::class.java)
        startActivity(intent)
    }
}