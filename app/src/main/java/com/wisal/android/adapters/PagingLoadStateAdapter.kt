package com.wisal.android.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wisal.android.data.paging.R
import com.wisal.android.data.paging.databinding.ItemLoadStateBinding

class PagingLoadStateAdapter<T: Any,ViewHolder: RecyclerView.ViewHolder> (
   private val adapter: PagingDataAdapter<T,ViewHolder>
):  LoadStateAdapter<PagingLoadStateAdapter.LoadStateItemViewHolder>(){


    override fun onBindViewHolder(holder: LoadStateItemViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadStateItemViewHolder {
        return LoadStateItemViewHolder(
            ItemLoadStateBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_load_state,
                        parent,
                        false
                    )
            )
        ) {
            adapter.retry()
        }
    }

    class LoadStateItemViewHolder(private val binding: ItemLoadStateBinding,retry: () -> Unit): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener {
                Log.d("Character","Retry invoke")
                retry.invoke()
            }
        }
        fun bind(loadState: LoadState) {
            Log.d("Charac","Load state adapter bind called")
            if(loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }

            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error

        }

    }
}