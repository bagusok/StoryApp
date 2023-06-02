package com.example.storyapp

import com.example.storyapp.model.ListStoryItem

object DataDummy {


    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "https://story-api.dicoding.dev/images/stories/photos-1685504951840_QjNE_6Sw.jpg",
                "2023-05-31T03:49:11.841Z",
                "Ahmad ${i.toString()}",
            )
            items.add(quote)
        }
        return items
    }
}
