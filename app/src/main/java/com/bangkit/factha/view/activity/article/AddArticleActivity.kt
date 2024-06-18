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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.factha.R
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.databinding.ActivityAddArticleBinding
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
    private val viewModelOcr by viewModels<OcrViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null
    private var currentImageUriOcr: Uri? = null

    private val uCropFunction = object : ActivityResultContract<List<Uri>, Uri>() {
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val uriInput = input[0]
            val uriOutput = input[1]

            val uCrop = UCrop.of(uriInput, uriOutput)

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
        }
    }

    private val cropImageOcr = registerForActivityResult(uCropFunction) { uri ->
        if (uri != Uri.EMPTY) {
            currentImageUriOcr = uri

            val imageTmp = ImageView(this)
            imageTmp.setImageURI(currentImageUriOcr)
            showImageAlertDialog(imageTmp)
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        }
    }

    private val launcherGalleryOcr = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUriOcr = uri
            showImageOcr()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setup()
        observeViewModelOcr()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(this)
        addArticleViewModel = ViewModelProvider(this, factory).get(AddArticleViewModel::class.java)
    }

    private fun setup() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnUploadArticle.setOnClickListener { startGallery() }
        binding.ocrIcon.setOnClickListener { startGalleryOcr() }
        binding.ocrInput.setOnClickListener { startGalleryOcr() }
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
            .setTitle(getString(R.string.masukkan_berita_anda_disini))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.oke)) { _, _ ->
                binding.edArticleForm.setText(dialogEditText.text.toString())
            }
            .setNegativeButton(getString(R.string.kembali), null)
            .create()

        dialog.show()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startGalleryOcr() {
        launcherGalleryOcr.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            val uriOutput = File(filesDir, "croppedImage.jpg").toUri()
            val listUri = listOf(uri, uriOutput)
            cropImage.launch(listUri)
        }
    }

    private fun showImageOcr() {
        currentImageUriOcr?.let { uri ->
            val uriOutput = File(filesDir, "croppedImageOcr.jpg").toUri()
            val listUri = listOf(uri, uriOutput)
            cropImageOcr.launch(listUri)
        }
    }


    private fun postOcr(imageBase64: String) {
        viewModelOcr.postOcr(imageBase64)
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
            showToast(getString(R.string.pastikan_semua_data_sudah_terisi))
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
                    showToast(getString(R.string.sedang_memposting_dan_memvalidasi_berita))
                }
                is Result.Success -> {
                    showToast(getString(R.string.berita_berhasil_diproses))
                    finish()
                }
                is Result.Error -> {
                    showToast(" ${result.error}")
                }
            }
        })
    }

    private fun observeViewModelOcr() {
        viewModelOcr.ocrResult.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.progressInformation.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.progressInformation.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.berhasil))
                        setMessage(getString(R.string.berhasil_mengambil_text_dari_gambar))
                        setPositiveButton(getString(R.string.oke), null)
                        val recognizedText = result.data.recognizedText ?: getString(R.string.tidak_dapat_mengambil_text)
                        binding.edArticleForm.setText(recognizedText.lowercase())
                        create()
                        show()
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.progressInformation.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.gagal))
                        setMessage(getString(R.string.gagal_saat_mengambil_text))
                        setPositiveButton(getString(R.string.oke), null)
                        create()
                        show()
                    }
                }
            }
        })
    }

    private fun showImageAlertDialog(image: ImageView) {
        val builder = AlertDialog.Builder(this)
            .setMessage("Scan Text from the Image")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                Log.i("tes aja ocr",currentImageUriOcr?.let { uriToBase64(it) } ?: "" )
                postOcr(currentImageUriOcr?.let { uriToBase64(it) } ?: "")
            }
            .setView(image)

        builder.create().show()
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
