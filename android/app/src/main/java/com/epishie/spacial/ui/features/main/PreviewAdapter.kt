package com.epishie.spacial.ui.features.main

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epishie.spacial.R
import com.epishie.spacial.databinding.PreviewItemBinding
import com.epishie.spacial.ui.PREVIEW_PAGE_SIZE

class PreviewAdapter : PagedListAdapter<Preview, RecyclerView.ViewHolder>(DiffCallback) {
    companion object {
        const val ITEM_TYPE = 0
        const val MORE_TYPE = 1
    }
    var onItemClickListener: ((String) -> Unit)? = null
    var onMoreClickListener: (() -> Unit)? = null
    private val actualCount: Int
        get() = super.getItemCount()

    override fun getItemViewType(position: Int): Int {
        return when {
            position < PREVIEW_PAGE_SIZE -> ITEM_TYPE
            else -> MORE_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            ITEM_TYPE -> {
                val binding = PreviewItemBinding.inflate(inflater,
                        parent, false)
                ViewHolder(binding).apply {
                    binding.root.setOnClickListener {
                        getItem(adapterPosition)?.let {
                            onItemClickListener?.invoke(it.id)
                        }
                    }
                }
            }
            MORE_TYPE -> {
                val view = inflater.inflate(R.layout.preview_more_item, parent, false)
                view.setOnClickListener {
                    onMoreClickListener?.invoke()
                }
                MoreViewHolder(view)
            }
            else -> throw IllegalStateException("Unknown item type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                getItem(position)?.let {
                    holder.bind(it)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return Math.min(actualCount, PREVIEW_PAGE_SIZE) + when {
            actualCount > PREVIEW_PAGE_SIZE -> 1
            else -> 0
        }
    }

    class ViewHolder(private val binding: PreviewItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(preview: Preview) {
            binding.preview = preview
        }
    }

    class MoreViewHolder(view: View) : RecyclerView.ViewHolder(view)

    object DiffCallback : DiffUtil.ItemCallback<Preview>() {
        override fun areItemsTheSame(oldItem: Preview, newItem: Preview): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Preview, newItem: Preview): Boolean {
            return oldItem == newItem
        }
    }
}