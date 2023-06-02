package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.database.StoryDatabase
import com.example.storyapp.networking.ApiClient

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiClient().createApiService()
        return StoryRepository(database, apiService)
    }
}