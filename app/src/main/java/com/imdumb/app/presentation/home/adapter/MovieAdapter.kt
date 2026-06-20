package com.imdumb.app.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.imdumb.app.R
import com.imdumb.app.databinding.ItemMovieBinding
import com.imdumb.app.domain.model.Movie

class MovieAdapter(private val onMovieClick: (Movie) -> Unit) :
    ListAdapter<Movie, MovieAdapter.MovieViewHolder>(DIFF_CALLBACK) {

    init {
        setHasStableIds(true)
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun getItemId(position: Int): Long = getItem(position).id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding, onMovieClick)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: MovieViewHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }

    class MovieViewHolder(private val binding: ItemMovieBinding, private val onMovieClick: (Movie) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) = with(binding) {
            movieTitle.text = movie.title
            movieRating.text = movie.rating?.let {
                root.context.getString(R.string.rating_format, it)
            } ?: root.context.getString(R.string.rating_unavailable)

            poster.contentDescription = root.context.getString(
                R.string.movie_poster_description,
                movie.title
            )
            Glide.with(poster)
                .load(movie.posterUrl)
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .centerCrop()
                .into(poster)

            root.setOnClickListener { onMovieClick(movie) }
        }

        fun recycle() = with(binding) {
            root.setOnClickListener(null)
            Glide.with(poster).clear(poster)
        }
    }

    private companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
        }
    }
}
