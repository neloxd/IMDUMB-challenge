package com.imdumb.app.presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.imdumb.app.databinding.ItemActorBinding

class ActorAdapter : ListAdapter<String, ActorAdapter.ActorViewHolder>(DIFF_CALLBACK) {

    init {
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val binding = ItemActorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ActorViewHolder(private val binding: ItemActorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(actor: String) {
            binding.actorName.text = actor
        }
    }

    private companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        }
    }
}
