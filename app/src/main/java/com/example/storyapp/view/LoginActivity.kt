package com.example.storyapp.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.model.*
import com.example.storyapp.networking.ApiClient
import com.example.storyapp.utils.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var _isLogout = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        playAnimation()

        val isLogout = intent.getBooleanExtra("isLogout", false)

        if (isLogout == true) {
            _isLogout = true
        }

        val userPreference = UserPreference(this@LoginActivity)
        val userData = userPreference.getUser()
        if (userData.token != "") {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val emailFromIntent: String? = intent.getStringExtra("email")
        binding.loginEmail.setText(emailFromIntent ?: "")

        binding.buttonTextRegister.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener{
            LoginIdlingResource.increment()
            binding.buttonLogin.isEnabled = false
            binding.buttonLogin.text = R.string.app_text_loading_indicator.toString()

            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                showAlert(this, R.string.app_text_fill_all_the_fields.toString())
                binding.buttonLogin.apply {
                    isEnabled = true
                    text = R.string.app_login_button.toString()
                }
                return@setOnClickListener
            }


            val apiClient = ApiClient()

            val apiService = apiClient.createApiService()

            val loginRequest = LoginRequest(email, password)

            val call = apiService.login(loginRequest)

            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val userId = loginResponse?.loginResult?.userId.toString()
                        val name = loginResponse?.loginResult?.name.toString()
                        val token = loginResponse?.loginResult?.token.toString()

                        userPreference.setUser(userId, name, token)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        LoginIdlingResource.decrement()
                        finish()

                    } else {
                        val errorBody = response.errorBody()?.string()
                        val jsonObject = JSONObject(errorBody.toString())
                        val message = jsonObject.getString("message")
                        showAlert(this@LoginActivity, message)


                    }
                    binding.buttonLogin.isEnabled = true
                    binding.buttonLogin.text = R.string.app_login_button.toString()

                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    showAlert(this@LoginActivity, "Error.")
                    Log.e("RegisterActivity", "onFailure: ${t.message}")
                    binding.buttonLogin.isEnabled = true
                    binding.buttonLogin.text = R.string.app_login_button.toString()
                }

            })




        }


    }

    private fun playAnimation() {
        val loginEmail = ObjectAnimator.ofFloat(binding.loginEmail, View.ALPHA, 1f).setDuration(500)
        val loginPassword = ObjectAnimator.ofFloat(binding.loginPassword, View.ALPHA, 1f).setDuration(500)
        val btn = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(loginEmail, loginPassword, btn)
            start()
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (_isLogout == true) {
                    finishAffinity()
                }else{
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }



}

