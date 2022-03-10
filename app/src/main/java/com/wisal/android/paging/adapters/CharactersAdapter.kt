package com.wisal.android.paging.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wisal.android.paging.R
import com.wisal.android.paging.databinding.ItemCharacterBinding
import com.wisal.android.paging.databinding.LineSeparatorBinding
import com.wisal.android.paging.models.Character
import com.wisal.android.paging.viewmodels.UiModel
import javax.inject.Inject

private object CharacterDiffUtil: DiffUtil.ItemCallback<UiModel>() {

    override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return (oldItem is UiModel.CharacterItem && newItem is UiModel.CharacterItem
                && oldItem.item.id == newItem.item.id && oldItem.item.name == newItem.item.name)
    }

    override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return oldItem == newItem
    }

}


class CharactersAdapter @Inject constructor(): PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(CharacterDiffUtil) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when(holder) {
                is CharacterViewHolder -> holder.bind((it as UiModel.CharacterItem).item)
                is SeparatorViewHolder -> holder.bind((it as UiModel.SeparatorModel).description)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_character -> CharacterViewHolder(
                ItemCharacterBinding.inflate(
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
            is UiModel.CharacterItem -> R.layout.item_character
            is UiModel.SeparatorModel -> R.layout.line_separator
            else -> throw IllegalStateException("Unknown view")
        }
    }

    inner class CharacterViewHolder(private val binding: ItemCharacterBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Character) = with(binding) {
            character = item
        }

    }

}

class SeparatorViewHolder(val binding: LineSeparatorBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(text: String) {
        binding.description.text= text
    }

}