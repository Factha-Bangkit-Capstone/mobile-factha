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

            settingProfile?.imageBase64?.let { base64Image ->
                val imageBytes = Base64.decode("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", Base64.DEFAULT)
                Glide.with(requireContext())
                    .asBitmap()
                    .load(imageBytes)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar) // Image to display if loading fails
                    .into(binding.imgProfile)
            }
        }
    }

//    private fun editProfile() {
//        val intent = Intent(requireContext(), EditProfileActivity::class.java)
//        startActivity(intent)
//    }

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