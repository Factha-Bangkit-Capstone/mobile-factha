package com.bangkit.factha.view.activity.article

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.factha.R
import com.bangkit.factha.databinding.ActivityAddArticleBinding

class AddArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddArticleBinding
    private var editText: EditText? = null
    private var spinnerTag: Spinner? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLongTextInput()
        setupSpinner()
        setup()
    }

    private fun setup(){
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupSpinner() {
        spinnerTag = binding.spinnerTag

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.pilihan_tag,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerTag?.adapter = adapter
    }

    private fun setupLongTextInput(){
        editText = binding.edArticleForm

        editText?.setOnClickListener {
            showLongTextInputDialog()
        }
    }

    private fun showLongTextInputDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_long_text_input, null)
        val dialogEditText = dialogView.findViewById<EditText>(R.id.et_long_text)

        dialogEditText.setText(editText?.text.toString())

        val dialog = AlertDialog.Builder(this)
            .setTitle("Masukkan berita Anda disini")
            .setView(dialogView)
            .setPositiveButton("Ok") { _, _ ->
                editText?.setText(dialogEditText.text.toString())
            }
            .setNegativeButton("Kembali", null)
            .create()

        dialog.show()
    }
}
