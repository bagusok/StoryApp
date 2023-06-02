package com.example.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.model.DetailStoryResponse
import com.example.storyapp.model.Story
import com.example.storyapp.networking.ApiClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStoryViewModel: ViewModel() {
    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getStoryDetail(token: String, storyId: String){
        _isLoading.value = true

        val apiClient = ApiClient()
        val apiService = apiClient.createApiService()
        val call =
            apiService.getStoryById("Bearer $token", storyId)

        call.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val detailResponse = response.body()
                    Log.d("RegisterActivity", "onResponses: ${detailResponse?.message}")

                    if (detailResponse?.error == false) {
                        _story.value = detailResponse.story!!
                    }

                    _isLoading.value = false


                } else {
                    val errorBody = response.errorBody()?.string()
                    val jsonObject = JSONObject(errorBody.toString())
                    val message = jsonObject.getString("message")
                    _isLoading.value = false
                }


            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                Log.e("RegisterActivity", "onFailure: ${t.message}")
                _isLoading.value = false
            }


        })
    }

}