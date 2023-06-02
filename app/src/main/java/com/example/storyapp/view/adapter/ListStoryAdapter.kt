package com.example.storyapp.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.CardListStoryBinding
import com.example.storyapp.model.ListStoryItem
import com.example.storyapp.view.DetailStory


class ListStoryAdapter :
    PagingDataAdapter<ListStoryItem, ListStoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CardListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: CardListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {

            binding.authorTop.text = data.name
            binding.cardDescription.text = data.description
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.cardListImage)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStory::class.java)
                intent.putExtra("story_id", data.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
