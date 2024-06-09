package com.bangkit.factha.view.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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
}