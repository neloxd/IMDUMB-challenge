package com.imdumb.app.presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.imdumb.app.R
import com.imdumb.app.databinding.ItemMovieImageBinding

class ImagePagerAdapter(private val movieTitle: String) :
    ListAdapter<String, ImagePagerAdapter.ImageViewHolder>(DIFF_CALLBACK) {

    init {
        setHasStableIds(true)
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemMovieImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding, movieTitle)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: ImageViewHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }

    class ImageViewHolder(private val binding: ItemMovieImageBinding, private val movieTitle: String) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) = with(binding) {
            image.contentDescription = root.context.getString(
                R.string.movie_image_description,
                movieTitle
            )
            Glide.with(image)
                .load(url.takeIf { it.isNotBlank() })
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .centerCrop()
                .into(image)
        }

        fun recycle() {
            Glide.with(binding.image.context.applicationContext).clear(binding.image)
        }
    }

    private companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        }
    }
}
