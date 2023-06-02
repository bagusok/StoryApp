package com.example.storyapp.networking
import android.content.SharedPreferences
import com.example.storyapp.model.*
import com.example.storyapp.utils.UserPreference
import com.example.storyapp.view.MainActivity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

//val token = MainActivity()
//val tokens = token.userPreference.getUser().token

//val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXdvN1hURDFLaGQyS3haU2oiLCJpYXQiOjE2ODU0NTg0MTZ9.htMGebQRY0aJy0nHDNloeaoFE9WMWLY0bJZWkczVmG8"

interface ApiService {


    @POST("register")
    fun register(
        @Body body: RegisterRequest
    ): Call<RegisterResponse>

    @POST("login")
    fun login(
        @Body body: LoginRequest
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): ListStoryResponse

    @GET("stories")
    fun getStoriesForWidget(
        @Header("Authorization") token: String,
    ): Call<ListStoryResponse>

    @GET("stories?location=1")
    fun getStoriesWithLocation(
        @Header("Authorization") token: String
    ): Call<ListStoryResponse>

    @GET("stories/{id}")
    fun getStoryById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?

    ): Call<UploadStoryResponse>




}