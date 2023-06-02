package com.example.storyapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.utils.UserPreference
import com.example.storyapp.utils.formatDate
import com.example.storyapp.viewmodel.DetailStoryViewModel

class DetailStory : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val storyDetailViewModel by viewModels<DetailStoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val storyId = intent.getStringExtra("story_id")
        val userPreference = UserPreference(this@DetailStory)
        val userData = userPreference.getUser()

        storyDetailViewModel.getStoryDetail(userData.token.toString(), storyId.toString())
        storyDetailViewModel.story.observe(this) {
            binding.apply {
                Glide.with(this@DetailStory)
                    .load(it.photoUrl)
                    .into(imageStory)
                description.text = it.description
                author.text = "by ${it.name}"
                date.text = "Published on ${formatDate(it.createdAt.toString())}"
            }
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               startActivity(Intent(this@DetailStory, MainActivity::class.java))
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }


}