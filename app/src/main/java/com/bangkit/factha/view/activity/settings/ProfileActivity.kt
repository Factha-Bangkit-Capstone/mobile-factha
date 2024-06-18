package com.bangkit.factha.view.activity.settings

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bangkit.factha.R
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.databinding.ActivityProfileBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.activity.MainActivity
import com.bumptech.glide.Glide
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var userPreferences: UserPreferences
    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val uCropFunction = object : ActivityResultContract<List<Uri>, Uri>() {
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val uriInput = input[0]
            val uriOutput = input[1]

            val uCrop = UCrop.of(uriInput, uriOutput)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(800, 800)

            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return intent?.let { UCrop.getOutput(it) } ?: Uri.EMPTY
        }

    }

    private val cropImage = registerForActivityResult(uCropFunction) { uri ->
        if (uri != Uri.EMPTY) {
            currentImageUri = uri
            binding.imgProfile.setImageURI(uri)
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            val uriOutput = File(filesDir, "croppedImage.jpg").toUri()

            val listUri = listOf(uri, uriOutput)
            cropImage.launch(listUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences.getInstance(dataStore)

        setEditText()
        setupTextWatchers()
        updateSaveButtonState()
        setupAction()
        observeViewModel()

        binding.btnBack.setOnClickListener { finish() }
        binding.changePhotoTv.setOnClickListener { startGallery() }
    }

    private fun setupAction() {
        binding.btnSubmit.setOnClickListener {
                val imageBase64 = currentImageUri?.let { uriToBase64(it) } ?: ""
                val name = binding.nameInputLayout.editText!!.text.toString()
                val email = binding.emailInputLayout.editText!!.text.toString()
                val oldPassword = binding.oldPasswordInputLayout.editText!!.text.toString()
                val newPassword = binding.newPasswordInputLayout.editText!!.text.toString()

                editProfile(imageBase64, name, email, "$name: change the profile", oldPassword, newPassword)
            }
    }


    private fun editProfile(image: String, name: String, email: String, body: String, oldPassword: String, newPassword: String) {
        viewModel.editProfile(image, name, email, body, oldPassword, newPassword)
    }

    private fun observeViewModel() {
        viewModel.editProfileResult.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showSuccessDialog()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to update profile. Check the data or your old password", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Profile Updated")
            .setMessage("Profile updated successfully. Press 'Back to Home' to return to the home screen.")
            .setPositiveButton("Back to Home") { dialog, _ ->
                dialog.dismiss()
                navigateToHome()
            }
            .show()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }


    private fun setEditText() {
        lifecycleScope.launch {
            userPreferences.userDetails.collect { userDetails ->
                if (userDetails != null) {
                    val imageBytes = Base64.decode(userDetails.imageB64, Base64.DEFAULT)
                    Glide.with(this@ProfileActivity)
                        .asBitmap()
                        .load(imageBytes)
                        .into(binding.imgProfile)
                    val premiumStatus = if (userDetails.premium == 1) "Premium" else "Non-Premium"
                    binding.nameInputLayout.editText?.text = Editable.Factory.getInstance().newEditable(userDetails.name)
                    binding.emailInputLayout.editText?.text = Editable.Factory.getInstance().newEditable(userDetails.email)
                    binding.premiumStatusInputLayout.editText?.text = Editable.Factory.getInstance().newEditable(premiumStatus)
                }
            }
        }
    }

    private fun uriToBase64(uri: Uri): String {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    private fun setupTextWatchers() {
        binding.nameInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.emailInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.emailInputLayout.error = if (isValidEmail(s.toString())) null else getString(
                    R.string.invalid_email)
                updateSaveButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.oldPasswordInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.oldPasswordInputLayout.error = if (isValidPassword(s.toString())) null else getString(R.string.password_must_be_at_least_8_characters_long)
                updateSaveButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.newPasswordInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.newPasswordInputLayout.error = if (isValidPassword(s.toString())) null else getString(R.string.password_must_be_at_least_8_characters_long)
                updateSaveButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateSaveButtonState() {
        val nameValid = binding.nameInputLayout.editText?.text?.isNotEmpty() ?: false
        val emailValid = isValidEmail(binding.emailInputLayout.editText?.text.toString())
        val oldPasswordValid = isValidPassword(binding.oldPasswordInputLayout.editText?.text.toString())
        val newPasswordValid = isValidPassword(binding.newPasswordInputLayout.editText?.text.toString())

        binding.btnSubmit.isEnabled = nameValid && emailValid && newPasswordValid && oldPasswordValid
        binding.btnSubmit.alpha = if (binding.btnSubmit.isEnabled) 1.0f else 0.5f
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }
}
