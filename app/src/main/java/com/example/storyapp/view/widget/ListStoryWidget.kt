package com.example.storyapp.view.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.storyapp.R

class ListStoryWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

}
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.list_story_widget)

    views.setRemoteAdapter(R.id.stack_view, Intent(context, StackWidgetService::class.java))
    views.setEmptyView(R.id.stack_view, R.id.empty_view)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

