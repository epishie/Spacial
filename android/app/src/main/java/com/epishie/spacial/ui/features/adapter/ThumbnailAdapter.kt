package com.epishie.spacial.ui.features.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.epishie.spacial.databinding.ThumbnailItemBinding

class ThumbnailAdapter : PagedListAdapter<Thumbnail, ThumbnailAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ThumbnailItemBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    class ViewHolder(private val binding: ThumbnailItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(thumbnail: Thumbnail) {
            binding.thumbnail = thumbnail
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Thumbnail>() {
        override fun areItemsTheSame(oldItem: Thumbnail, newItem: Thumbnail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Thumbnail, newItem: Thumbnail): Boolean {
            return oldItem == newItem
        }
    }
}