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
import android.view.animation.AnimationUtils
import android.widget.Toast
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
        val builder = android.app.AlertDialog.Builder(requireContext())
        val languages = resources.getStringArray(R.array.language_options)
        builder.setTitle(getString(R.string.pilih_bahasa))
        builder.setItems(languages) { _, which ->
            val selectedLanguage = languages[which]

            Toast.makeText(requireContext(),
                getString(R.string.bahasa_yang_dipilih, selectedLanguage), Toast.LENGTH_SHORT).show()
        }
        builder.show()
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
}