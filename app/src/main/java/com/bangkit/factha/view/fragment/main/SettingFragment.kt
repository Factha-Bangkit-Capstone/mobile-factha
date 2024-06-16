package com.bangkit.factha.view.fragment.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import com.bangkit.factha.R
import com.bangkit.factha.databinding.FragmentSettingBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.activity.settings.AboutActivity
import com.bangkit.factha.view.activity.splashscreen.SplashScreenActivity
import com.bumptech.glide.Glide
import android.util.Base64
import com.bangkit.factha.view.activity.settings.ProfileActivity
import kotlin.io.encoding.ExperimentalEncodingApi

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SettingViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.btnEditProfile.setOnClickListener{ editProfile() }
//        binding.cardLanguage.setOnClickListener { selectLanguage() }
//        binding.cardNotification.setOnClickListener {  }
        binding.cardAbout.setOnClickListener { selectAbout() }
        binding.cardLogout.setOnClickListener { logout() }
        binding.btnEditProfile.setOnClickListener{ selectProfile() }

        viewModel.getSettingProfile().observe(viewLifecycleOwner) { settingProfile ->
            binding.tvUsername.text = settingProfile?.name ?: ""
            binding.tvEmail.text = settingProfile?.email ?: ""

            settingProfile?.imageBase64?.let {
                val imageBytes = Base64.decode(settingProfile.imageBase64, Base64.DEFAULT)
                Glide.with(requireContext())
                    .asBitmap()
                    .load(imageBytes)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar) // Image to display if loading fails
                    .into(binding.imgProfile)
            }
        }
    }

//    private fun selectLanguage() {
//        val builder = Builder(requireContext())
//        val languages = resources.getStringArray(R.array.language_options)
//        builder.setTitle("Pick a language")
//        builder.setItems(languages) { dialog, which ->
//            val selectedLanguage = languages[which]
//
//            Toast.makeText(requireContext(), "Selected language: $selectedLanguage", Toast.LENGTH_SHORT).show()
//        }
//        builder.show()
//    }

    private fun selectAbout() {
        val intent = Intent(requireContext(), AboutActivity::class.java)
        startActivity(intent)
    }

    private fun selectProfile() {
        val intent = Intent(requireContext(), ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Logout")
            setMessage("Are you sure you want to logout?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.logout()
                val intent = Intent(requireContext(), SplashScreenActivity::class.java)
                startActivity(intent)
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}