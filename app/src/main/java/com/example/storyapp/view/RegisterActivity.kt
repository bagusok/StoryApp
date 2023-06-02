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
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.model.RegisterRequest
import com.example.storyapp.model.RegisterResponse
import com.example.storyapp.networking.ApiClient
import com.example.storyapp.utils.UserPreference
import com.example.storyapp.utils.showAlert
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        playAnimation()

        val userPreference = UserPreference(this@RegisterActivity)
        val userData = userPreference.getUser()
        if (userData.token != "") {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.buttonTextLogin.setOnClickListener{
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.buttonRegister.setOnClickListener {
            binding.buttonRegister.isEnabled = false
            binding.buttonRegister.text = R.string.app_text_loading_indicator.toString()

            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            val username = binding.nameForm.text.toString()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                showAlert(this, R.string.app_text_fill_all_the_fields.toString())
                binding.buttonRegister.isEnabled = true
                binding.buttonRegister.text = R.string.app_register_button.toString()
                return@setOnClickListener
            }



            val apiClient = ApiClient()

            val apiService = apiClient.createApiService()

            val registerRequest = RegisterRequest(
               username, email, password
            )

            val call = apiService.register(registerRequest)

            call.enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        Log.d("RegisterActivity", "onResponse: ${registerResponse?.message}")
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val jsonObject = JSONObject(errorBody.toString())
                        val message = jsonObject.getString("message")
                        showAlert(this@RegisterActivity, message)
                    }

                    binding.buttonRegister.isEnabled = true
                    binding.buttonRegister.text = R.string.app_register_button.toString()
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    showAlert(this@RegisterActivity, "Error")
                    Log.e("RegisterActivity", "onFailure: ${t.message}")
                    binding.buttonRegister.isEnabled = true
                    binding.buttonRegister.text = R.string.app_register_button.toString()
                }
            })
        }
    }

    private fun playAnimation() {
        val loginEmail = ObjectAnimator.ofFloat(binding.loginEmail, View.ALPHA, 1f).setDuration(500)
        val loginPassword = ObjectAnimator.ofFloat(binding.loginPassword, View.ALPHA, 1f).setDuration(500)
        val nameForm = ObjectAnimator.ofFloat(binding.nameForm, View.ALPHA, 1f).setDuration(500)
        val btn = ObjectAnimator.ofFloat(binding.buttonRegister, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(nameForm, loginEmail, loginPassword, btn)
            start()
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
              finishAffinity()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


}

