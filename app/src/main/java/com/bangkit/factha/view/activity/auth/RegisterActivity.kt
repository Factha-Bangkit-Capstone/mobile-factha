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
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bangkit.factha.R
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.databinding.ActivityRegisterBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.activity.MainActivity

class RegisterActivity : AppCompatActivity() {
    private var isPasswordVisible = false
    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

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
        observeViewModel()
        setupAction()
    }

    private fun register(name: String, email: String, password: String) {
        viewModel.register(name ,email, password)
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

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.nameEtRegister.text.toString()
            val email = binding.emailEtRegister.text.toString()
            val password = binding.passwordEtRegister.text.toString()

            register(name, email, password)
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

    private fun observeViewModel() {
        viewModel.registerResult.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle("Success!")
                        setMessage("Success Create Account!")
                        setPositiveButton("Login") { _, _ ->
                            val intent = Intent(context, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        create()
                        show()
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle("Fail!")
                        setMessage("Can't Create Account!")
                        setPositiveButton("OK", null)
                        create()
                        show()
                    }
                }
            }
        })
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}