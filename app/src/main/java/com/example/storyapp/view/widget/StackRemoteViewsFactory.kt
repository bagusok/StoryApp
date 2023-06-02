package com.example.storyapp.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.model.ListStoryItem
import com.example.storyapp.model.ListStoryResponse
import com.example.storyapp.networking.ApiClient
import com.example.storyapp.utils.UserPreference
import com.example.storyapp.view.DetailStory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class StackRemoteViewsFactory(private val mContext: Context): RemoteViewsService.RemoteViewsFactory {

    private val stories = ArrayList<ListStoryItem>()

    private val userPreference = UserPreference(mContext)
    val token = userPreference.getUser().token

    override fun onCreate() {

        val apiClient = ApiClient()
        val apiService = apiClient.createApiService()

        val call = apiService.getStoriesForWidget("Bearer $token")
        call.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {

                if (response.isSuccessful) {
                    val list = response.body()
                    if (list != null) {
                        val allList = list.listStory as ArrayList<ListStoryItem>
                        stories.addAll(allList)
                    }
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                Log.e("StackRemoteViewsFactory", "onFailure: ${t.message}")
            }

        })

    }

    override fun onDataSetChanged() {
        Log.e("StackRemoteViewsFactory", "onCreate: $token")

        val apiClient = ApiClient()
        val apiServices = apiClient.createApiService()
        val call = apiServices.getStoriesForWidget("Bearer $token")
        call.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {

                if (response.isSuccessful) {
                    val list = response.body()
                    Log.e("Ini STACK RESPONSE", "STACK RESPONSE: ${list?.listStory}")
                    if (list != null) {
                        val allList = list.listStory as ArrayList<ListStoryItem>
                        stories.clear()
                        stories.addAll(allList)
                    }
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                Log.e("StackRemoteViewsFactory", "onFailure: ${t.message}")
            }

        })
        val views = RemoteViews(mContext.packageName, R.layout.list_story_widget)
        views.setRemoteAdapter(R.id.stack_view, Intent(mContext, StackWidgetService::class.java))
        views.setEmptyView(R.id.stack_view, R.id.empty_view)

       updateAppWidget(mContext, AppWidgetManager.getInstance(mContext), 1)

    }

    override fun onDestroy() {
        stories.clear()
    }

    override fun getCount(): Int {
        return stories.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val story = stories[position]
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setTextViewText(R.id.author, story.name)
        val imageUrl = story.photoUrl
        val bitmap = Glide.with(mContext)
            .asBitmap()
            .load(imageUrl)
            .submit()
            .get()
        rv.setImageViewBitmap(R.id.imageStory, bitmap)

       rv.setOnClickPendingIntent(R.id.imageStory, getPendingIntent(story))
        return rv
    }

    override fun getLoadingView(): RemoteViews {
        val views = RemoteViews(mContext.packageName, R.layout.list_story_widget)
        views.setTextViewText(R.id.banner_text,"Loading...")

        return views
    }

    override fun getViewTypeCount(): Int {
        return  1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    private fun getPendingIntent(story: ListStoryItem): PendingIntent {
        val intent = Intent(mContext, DetailStory::class.java)
        intent.putExtra("story_id", story.id)
        return  PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    }
}