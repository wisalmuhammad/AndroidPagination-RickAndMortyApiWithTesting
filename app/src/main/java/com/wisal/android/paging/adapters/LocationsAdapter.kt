package com.wisal.android.paging.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wisal.android.paging.R
import com.wisal.android.paging.databinding.ItemLocationBinding
import com.wisal.android.paging.databinding.LineSeparatorBinding
import com.wisal.android.paging.models.Location
import com.wisal.android.paging.viewmodels.UiModel
import javax.inject.Inject

private val LOCATION_DIFF_UTIL = object : DiffUtil.ItemCallback<UiModel>() {
    override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return (oldItem is UiModel.LocationItem && newItem is UiModel.LocationItem
                && oldItem.location.id == newItem.location.id)
    }

    override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return oldItem == newItem
    }

}

class LocationsAdapter @Inject constructor(): PagingDataAdapter<UiModel,RecyclerView.ViewHolder>(LOCATION_DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.item_location -> LocationItemViewHolder(
                ItemLocationBinding.inflate(
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when(it) {
                is UiModel.LocationItem -> (holder as LocationItemViewHolder).bind(it.location)
                is UiModel.SeparatorModel -> (holder as SeparatorViewHolder).bind(it.description)
                else -> throw IllegalArgumentException("Unknown view holder")
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is UiModel.LocationItem -> R.layout.item_location
            is UiModel.SeparatorModel -> R.layout.line_separator
            else -> throw IllegalStateException("Unknown view")
        }
    }

    class LocationItemViewHolder(private val binding: ItemLocationBinding): RecyclerView.ViewHolder(binding.root) {

        fun  bind(item: Location) {
            binding.location = item
        }

    }


}