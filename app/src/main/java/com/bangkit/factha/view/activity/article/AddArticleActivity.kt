package com.bangkit.factha.view.activity.article

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.factha.R
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.network.ApiConfig
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.databinding.ActivityAddArticleBinding
import com.bangkit.factha.view.activity.article.AddArticleViewModel
import com.bangkit.factha.view.ViewModelFactory
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class AddArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddArticleBinding
    private lateinit var addArticleViewModel: AddArticleViewModel
    private var currentImageUri: Uri? = null

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
            binding.ivThumbnailImageUploaded.setImageURI(uri)
            binding.ivImagePreview.setImageURI(uri)
            Log.i("tes in base64 img encode", uriToBase64(currentImageUri!!))
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setup()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(this)
        addArticleViewModel = ViewModelProvider(this, factory).get(AddArticleViewModel::class.java)
    }

    private fun setup() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnUploadArticle.setOnClickListener { startGallery() }
        binding.btnSubmit.setOnClickListener { submitArticle() }
        setupTitle()
        setupLongTextInput()
        setupSpinner()
    }

    private fun setupTitle() {
        binding.edTitleForm.addTextChangedListener { text ->
            updatePreviewTitle(text.toString())
        }
    }

    private fun setupSpinner() {
        val spinnerTag = binding.spinnerTag

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.pilihan_tag,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerTag.adapter = adapter

        // Set up listener for spinner to update preview
        spinnerTag.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedTag = parent.getItemAtPosition(position).toString()
                updatePreviewTag(selectedTag)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun setupLongTextInput(){
        val editText = binding.edArticleForm

        editText.setOnClickListener {
            showLongTextInputDialog()
        }
    }

    private fun showLongTextInputDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_long_text_input, null)
        val dialogEditText = dialogView.findViewById<EditText>(R.id.et_long_text)

        dialogEditText.setText(binding.edArticleForm.text.toString())

        val dialog = AlertDialog.Builder(this)
            .setTitle("Masukkan berita Anda disini")
            .setView(dialogView)
            .setPositiveButton("Ok") { _, _ ->
                binding.edArticleForm.setText(dialogEditText.text.toString())
            }
            .setNegativeButton("Kembali", null)
            .create()

        dialog.show()
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

    private fun updatePreviewTitle(title: String) {
        binding.tvTitlePreview.text = title
    }

    private fun updatePreviewTag(tag: String) {
        binding.tvPreviewTag.text = tag
    }

    private fun submitArticle() {
        val title = binding.edTitleForm.text.toString()
        val tag = binding.spinnerTag.selectedItem.toString()
        val body = binding.edArticleForm.text.toString()

        if (title.isEmpty() || body.isEmpty() || currentImageUri == null) {
            showToast("Please fill in all fields and select an image.")
            return
        }

        val userPreferences = UserPreferences.getInstance(this.dataStore)
        val token = runBlocking { userPreferences.token.first() }
        val userId = runBlocking { userPreferences.userId.first() }

        val imageBase64 = currentImageUri?.let { uriToBase64(it) } ?: ""

        if (token != null && userId != null) {
            addArticleViewModel.postNews(token, userId, title, tag, body, imageBase64)
        }

        addArticleViewModel.postNewsResult.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    showToast("Posting news, please wait...")
                }
                is Result.Success -> {
                    showToast("News posted successfully!")
                    finish()
                }
                is Result.Error -> {
                    showToast("Error posting news: ${result.error}")
                }
            }
        })
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
