package com.mikeschvedov.whatshouldiwatch.utils.paging

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.databinding.ChildRvViewHolderBinding
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import com.mikeschvedov.whatshouldiwatch.utils.Constants

class PagingAdapter : PagingDataAdapter<TmdbItem, PagingAdapter.MediaViewHolder>(DIFFUTIL) {

    class MediaViewHolder(private val binding: ChildRvViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TmdbItem){
            binding.apply{
                val imageAndPath = Constants.IMAGE_LOCATION+item.posterPath
                Glide.with(itemView)
                    .load(imageAndPath)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_baseline_error_24)
                    .into(imageviewChildItem)

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ChildRvViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null){
            holder.bind(currentItem)
        }
    }


    companion object {
        private val DIFFUTIL = object : DiffUtil.ItemCallback<TmdbItem>() {
            override fun areItemsTheSame(oldItem: TmdbItem, newItem: TmdbItem): Boolean =
                oldItem.id == newItem.id


            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: TmdbItem, newItem: TmdbItem) =
                oldItem == newItem

        }
    }
}