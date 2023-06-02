package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.storyapp.database.StoryDatabase
import com.example.storyapp.model.ListStoryItem
import com.example.storyapp.networking.ApiService

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService ) {

    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, "Bearer $token"),
            pagingSourceFactory = {
//                StoryPagingSource(apiService)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

}
