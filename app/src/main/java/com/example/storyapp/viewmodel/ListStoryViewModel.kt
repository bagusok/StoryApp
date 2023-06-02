package com.example.storyapp.viewmodel

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.model.ListStoryItem

class ListStoryViewModel(storyRepository: StoryRepository, token: String): ViewModel() {

    val story: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory(token).cachedIn(viewModelScope)


}

class ViewModelFactory(private val context: Context, token: String) : ViewModelProvider.Factory {
    private val token = token
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListStoryViewModel(Injection.provideRepository(context), token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}