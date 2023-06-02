package com.example.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.model.ListStoryResponse
import com.example.storyapp.networking.ApiClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListStoryWithLocationViewModel: ViewModel() {
    private val _stories = MutableLiveData<ListStoryResponse>()
    val stories: LiveData<ListStoryResponse> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getStoriesWithLocation(token: String?){
        _isLoading.value = true

        val apiClient = ApiClient()
        val apiService = apiClient.createApiService()
        val call = apiService.getStoriesWithLocation("Bearer $token")

        call.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    _stories.value = loginResponse as ListStoryResponse
                    Log.d("ListStoryWithLocation", "onResponses: ${loginResponse.message}")

                } else {
                    val errorBody = response.errorBody()?.string()
                    val jsonObject = JSONObject(errorBody.toString())
                    val message = jsonObject.getString("message")

                }

                _isLoading.value = false

            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("RegisterActivity", "onFailure: ${t.message}")
            }

        })



    }

}