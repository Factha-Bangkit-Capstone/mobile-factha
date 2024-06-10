package com.bangkit.factha.view.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.factha.R
import com.bangkit.factha.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private var isPasswordVisible = false
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        binding.emailEtRegister.addTextChangedListener(emailTextWatcher)
        binding.passwordEtRegister.addTextChangedListener(passwordTextWatcher)
        binding.nameEtRegister.addTextChangedListener(nameTextWatcher)
        binding.btnRegister.isEnabled = false

        setContentView(binding.root)
        setupPasswordToggle()
        setupLoginNavigation()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordToggle() {
        binding.passwordEtRegister.setOnTouchListener(View.OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.passwordEtRegister.right - binding.passwordEtRegister.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    val selection = binding.passwordEtRegister.selectionEnd
                    if (isPasswordVisible) {
                        binding.passwordEtRegister.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.passwordEtRegister.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_close, 0)
                    } else {
                        binding.passwordEtRegister.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        binding.passwordEtRegister.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    }
                    binding.passwordEtRegister.setSelection(selection)
                    isPasswordVisible = !isPasswordVisible
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun setupLoginNavigation() {
        binding.loginTv.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private val nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validateName()
            validateFields()
        }
    }

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validateEmail()
            validateFields()
        }
    }

    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validatePassword()
            validateFields()
        }
    }

    private fun validateName() {
        val email = binding.nameEtRegister.text.toString().trim()
        if (TextUtils.isEmpty(email)) {
            binding.emailAlert.visibility = View.VISIBLE
            binding.emailAlert.text = getString(R.string.email_can_t_empty)
        } else {
            binding.emailAlert.visibility = View.GONE
        }
    }

    private fun validateEmail() {
        val email = binding.emailEtRegister.text.toString().trim()
        if (TextUtils.isEmpty(email)) {
            binding.emailAlert.visibility = View.VISIBLE
            binding.emailAlert.text = getString(R.string.email_can_t_empty)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailAlert.visibility = View.VISIBLE
            binding.emailAlert.text = getString(R.string.invalid_email)
        } else {
            binding.emailAlert.visibility = View.GONE
        }
    }

    private fun validatePassword() {
        val password = binding.passwordEtRegister.text.toString().trim()
        if (TextUtils.isEmpty(password)) {
            binding.passwordAlert.text = getString(R.string.password_can_t_empty)
            binding.passwordAlert.visibility = View.VISIBLE
        } else if (password.length < MIN_PASSWORD_LENGTH) {
            binding.passwordAlert.text = getString(R.string.password_must_be_at_least_8_characters_long)
            binding.passwordAlert.visibility = View.VISIBLE
        } else {
            binding.passwordAlert.visibility = View.GONE
        }
    }


    private fun validateFields(){
        val email = binding.emailEtRegister.text.toString().trim()
        val password = binding.passwordEtRegister.text.toString().trim()
        val name = binding.nameEtRegister.text.toString().trim()

        val isEmailValid = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = !TextUtils.isEmpty(password) && password.length >= MIN_PASSWORD_LENGTH
        val isNameValid = !TextUtils.isEmpty(name)

        binding.btnRegister.isEnabled = isEmailValid && isPasswordValid && isNameValid
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}