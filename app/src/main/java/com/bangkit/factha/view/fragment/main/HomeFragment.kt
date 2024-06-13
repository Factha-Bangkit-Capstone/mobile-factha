package com.bangkit.factha.view.fragment.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.factha.R
import com.bangkit.factha.databinding.FragmentHomeBinding
import com.bangkit.factha.databinding.FragmentSettingBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.activity.MainViewModel
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.view.adapter.HomeAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = UserPreferences.getInstance(requireContext().dataStore)
        val token = runBlocking { userPreferences.token.first() }

        Log.d("tokentoken", "$token")

        if (token != null) {
            observeNews(token)
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvSelectedForYou.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeNews(token: String) {
        viewModel.getNews(token)
    }
}