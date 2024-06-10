package com.bangkit.factha.view.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.factha.R
import com.bangkit.factha.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private var isPasswordVisible = false
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        binding.emailEtLogin.addTextChangedListener(emailTextWatcher)
        binding.passwordEtLogin.addTextChangedListener(passwordTextWatcher)
        binding.btnLogin.isEnabled = false


        setContentView(binding.root)
        setupPasswordToggle()
        setupRegisterNavigation()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordToggle() {
        binding.passwordEtLogin.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.passwordEtLogin.right - binding.passwordEtLogin.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    val selection = binding.passwordEtLogin.selectionEnd
                    if (isPasswordVisible) {
                        binding.passwordEtLogin.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.passwordEtLogin.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_close, 0)
                    } else {
                        binding.passwordEtLogin.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        binding.passwordEtLogin.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                    }
                    binding.passwordEtLogin.setSelection(selection)
                    isPasswordVisible = !isPasswordVisible
                    return@OnTouchListener true
                }
            }
            false
        })
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

    private fun validateEmail() {
        val email = binding.emailEtLogin.text.toString().trim()
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
        val password = binding.passwordEtLogin.text.toString().trim()
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

    private fun setupRegisterNavigation() {
        binding.registerTv.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateFields(){
        val email = binding.emailEtLogin.text.toString().trim()
        val password = binding.passwordEtLogin.text.toString().trim()

        val isEmailValid = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = !TextUtils.isEmpty(password) && password.length >= MIN_PASSWORD_LENGTH

        binding.btnLogin.isEnabled = isEmailValid && isPasswordValid
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
