package com.imdumb.app.presentation.home.adapter

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.imdumb.app.databinding.ItemCategoryBinding
import com.imdumb.app.domain.model.Movie
import com.imdumb.app.domain.model.MovieCategory

class CategoryAdapter(private val onMovieClick: (Movie) -> Unit) :
    ListAdapter<MovieCategory, CategoryAdapter.CategoryViewHolder>(DIFF_CALLBACK) {

    private val sharedViewPool = RecyclerView.RecycledViewPool()
    private val horizontalStates = mutableMapOf<String, Parcelable?>()

    init {
        setHasStableIds(true)
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        sharedViewPool.setMaxRecycledViews(DEFAULT_MOVIE_VIEW_TYPE, MAX_SHARED_MOVIE_HOLDERS)
    }

    override fun getItemId(position: Int): Long = getItem(position).id.hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding, sharedViewPool, onMovieClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, horizontalStates[category.id])
    }

    override fun onViewRecycled(holder: CategoryViewHolder) {
        holder.boundCategoryId?.let { categoryId ->
            horizontalStates[categoryId] = holder.saveScrollState()
        }
        super.onViewRecycled(holder)
    }

    override fun onCurrentListChanged(previousList: List<MovieCategory>, currentList: List<MovieCategory>) {
        super.onCurrentListChanged(previousList, currentList)
        val activeCategoryIds = currentList.mapTo(mutableSetOf()) { it.id }
        horizontalStates.keys.retainAll(activeCategoryIds)
    }

    class CategoryViewHolder(
        private val binding: ItemCategoryBinding,
        sharedViewPool: RecyclerView.RecycledViewPool,
        onMovieClick: (Movie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val movieAdapter = MovieAdapter(onMovieClick)
        private val movieLayoutManager = LinearLayoutManager(
            binding.root.context,
            RecyclerView.HORIZONTAL,
            false
        ).apply {
            initialPrefetchItemCount = 4
        }

        var boundCategoryId: String? = null
            private set

        init {
            binding.moviesRecycler.apply {
                layoutManager = movieLayoutManager
                adapter = movieAdapter
                setHasFixedSize(true)
                setRecycledViewPool(sharedViewPool)
                isNestedScrollingEnabled = false
                setItemViewCacheSize(6)
            }
        }

        fun bind(category: MovieCategory, savedState: Parcelable?) = with(binding) {
            boundCategoryId = category.id
            categoryTitle.text = category.name
            movieAdapter.submitList(category.movies) {
                if (boundCategoryId != category.id) return@submitList
                if (savedState != null) {
                    movieLayoutManager.onRestoreInstanceState(savedState)
                } else {
                    movieLayoutManager.scrollToPositionWithOffset(0, 0)
                }
            }
        }

        fun saveScrollState(): Parcelable? = movieLayoutManager.onSaveInstanceState()
    }

    private companion object {
        const val DEFAULT_MOVIE_VIEW_TYPE = 0
        const val MAX_SHARED_MOVIE_HOLDERS = 24

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieCategory>() {
            override fun areItemsTheSame(oldItem: MovieCategory, newItem: MovieCategory): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MovieCategory, newItem: MovieCategory): Boolean =
                oldItem == newItem
        }
    }
}
