package com.wisal.android.paging.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wisal.android.paging.R
import com.wisal.android.paging.databinding.ItemEpisodeBinding
import com.wisal.android.paging.databinding.LineSeparatorBinding
import com.wisal.android.paging.models.Episode
import com.wisal.android.paging.viewmodels.UiModel
import java.lang.Exception
import javax.inject.Inject


private val EPISODE_DIFF_UTIL = object : DiffUtil.ItemCallback<UiModel>()  {
    override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return (oldItem is UiModel.EpisodeItem && newItem is UiModel.EpisodeItem
                && oldItem.episode.id == newItem.episode.id)
    }

    override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return oldItem == newItem
    }

}

class EpisodesAdapter @Inject constructor(): PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(EPISODE_DIFF_UTIL) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when(it) {
                is UiModel.EpisodeItem -> (holder as EpisodeItemViewHolder).bind(it.episode)
                is UiModel.SeparatorModel -> (holder as SeparatorViewHolder).bind(it.description)
                else -> throw Exception("Unknown type")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_episode -> EpisodeItemViewHolder(
                ItemEpisodeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> SeparatorViewHolder(
                LineSeparatorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is UiModel.EpisodeItem -> R.layout.item_episode
            is UiModel.SeparatorModel -> R.layout.line_separator
            else -> throw UnsupportedOperationException("Unknown view")
        }
    }



    class EpisodeItemViewHolder(private val binding: ItemEpisodeBinding)
        : RecyclerView.ViewHolder(binding.root) {

            fun bind(episode: Episode) {
                binding.episode = episode
            }

    }
}