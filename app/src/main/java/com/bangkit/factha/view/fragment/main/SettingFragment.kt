package com.bangkit.factha.view.fragment.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.bangkit.factha.databinding.FragmentSettingBinding
import com.bangkit.factha.view.activity.splashscreen.SplashScreenActivity
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.btnEditProfile.setOnClickListener{ editProfile() }
//        binding.cardLanguage.setOnClickListener { selectLanguage() }
//        binding.cardNotification.setOnClickListener {  }
//        binding.cardAbout.setOnClickListener { selectAbout() }
        binding.cardLogout.setOnClickListener { logout() }
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

//    private fun selectAbout() {
//        val intent = Intent(requireContext(), AboutActivity::class.java)
//        startActivity(intent)
//    }

    private fun logout() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Logout")
            setMessage("Are you sure you want to logout?")
            setPositiveButton("Yes") { _, _ ->
                //viewModel.logout()
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