package com.bangkit.factha.view.fragment.main

import android.app.AlertDialog.Builder
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bangkit.factha.R
import com.bangkit.factha.databinding.FragmentSettingBinding
import com.bangkit.factha.view.activity.settings.AboutActivity
import com.bangkit.factha.view.activity.settings.EditProfileActivity
import com.bangkit.factha.view.activity.splashscreen.SplashScreenActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEditProfile.setOnClickListener{ editProfile() }
        binding.cardLanguage.setOnClickListener { selectLanguage() }
        binding.cardNotification.setOnClickListener {  }
        binding.cardAbout.setOnClickListener { selectAbout() }
        binding.cardLogout.setOnClickListener { logout() }
    }

    private fun editProfile() {
        val intent = Intent(requireContext(), EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun selectLanguage() {
        val builder = Builder(requireContext())
        val languages = resources.getStringArray(R.array.language_options)
        builder.setTitle("Pick a language")
        builder.setItems(languages) { dialog, which ->
            val selectedLanguage = languages[which]

            Toast.makeText(requireContext(), "Selected language: $selectedLanguage", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun selectAbout() {
        val intent = Intent(requireContext(), AboutActivity::class.java)
        startActivity(intent)
    }

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}