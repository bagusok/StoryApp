package com.example.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.model.ListStoryItem
import com.example.storyapp.networking.ApiClient

import com.example.storyapp.networking.ApiService
import com.example.storyapp.view.INI_TOKEN


class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        var datas = listOf<ListStoryItem>()
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val apiCLient = ApiClient().createApiService()
            val response = apiCLient.getStories(INI_TOKEN,page, params.loadSize)

            Log.e("INI DARI 1", "onSucceess ${response}")
            LoadResult.Page(
                data = response.listStory as List<ListStoryItem>,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.listStory.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            Log.e("Errrr", "onFailure: ${exception.message}")
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}