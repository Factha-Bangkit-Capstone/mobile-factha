package com.bangkit.factha.view.fragment.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
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
import android.view.animation.AnimationUtils
import com.bangkit.factha.view.activity.settings.ProfileActivity
import java.util.Locale
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

        val transition = AnimationUtils.loadAnimation(requireContext(), R.anim.transition_card_item)

        binding.cardLanguage.apply {
            visibility = View.VISIBLE
            startAnimation(transition)
        }
        binding.cardAbout.apply {
            visibility = View.VISIBLE
            startAnimation(transition)
        }
        binding.cardNotification.apply {
            visibility = View.VISIBLE
            startAnimation(transition)
        }
        binding.cardLogout.apply {
            visibility = View.VISIBLE
            startAnimation(transition)
        }

        binding.cardLanguage.setOnClickListener { selectLanguage() }
        binding.cardNotification.setOnClickListener { selectNotification()  }
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
                    .into(binding.imgProfile)
            }
        }
    }

    private fun selectLanguage() {
        val languages = resources.getStringArray(R.array.language_options)
        val currentLanguage = Locale.getDefault().language

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.pilih_bahasa))
        builder.setSingleChoiceItems(languages, -1) { dialog, which ->
            val selectedLanguage = when (which) {
                0 -> "in"
                1 -> "en"
                else -> {
                    "in"
                }
            }

            saveLanguagePreference(selectedLanguage)

            setLocale(selectedLanguage)
            restartActivity()

            dialog.dismiss()
        }
        builder.show()
    }

    private fun saveLanguagePreference(languageCode: String) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPrefs.edit().putString("language", languageCode).apply()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun restartActivity() {
        requireActivity().finish()
        val intent = requireActivity().intent
        startActivity(intent)
    }

    private fun selectNotification() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.pengembangan))
            setMessage(getString(R.string.fitur_masih_dalam_masa_pengembangan))
            setPositiveButton(getString(R.string.tutup), null)
            create()
            show()
        }
    }

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
            setTitle(getString(R.string.keluar))
            setMessage(getString(R.string.apakah_anda_yakin_keluar_akun))
            setPositiveButton(getString(R.string.konfirmasi)) { _, _ ->
                viewModel.logout()
                val intent = Intent(requireContext(), SplashScreenActivity::class.java)
                startActivity(intent)
            }
            setNegativeButton(getString(R.string.batal)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}