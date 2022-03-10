package com.wisal.android.paging.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.wisal.android.paging.adapters.LocationsAdapter
import com.wisal.android.paging.adapters.PagingLoadStateAdapter
import com.wisal.android.paging.databinding.LocationsFragmentBinding
import com.wisal.android.paging.viewmodels.LocationsViewModel
import com.wisal.android.paging.viewmodels.UiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationsFragment : Fragment() {


    @Inject lateinit var adapter: LocationsAdapter

    private lateinit var binding: LocationsFragmentBinding
    private val viewModel by viewModels<LocationsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LocationsFragmentBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            list.adapter = adapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter(adapter)
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindList(
            pagingData = viewModel.locations,
            adapter = adapter
        )
    }

}

private fun LocationsFragmentBinding.bindList(
    pagingData: Flow<PagingData<UiModel>>,
    adapter: LocationsAdapter
){

    retryButton.setOnClickListener {
        adapter.retry()
    }

    lifecycleOwner?.lifecycleScope?.launch {

        adapter.loadStateFlow.collect { loadState ->

            list.isVisible = loadState.source.refresh is LoadState.NotLoading
            progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            retryButton.isVisible = loadState.source.refresh is LoadState.Error && adapter.itemCount == 0

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.refresh as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error

            errorState?.let {
                Toast.makeText(retryButton.context,
                    "\uD83D\uDE28 Wooops ${it.error.localizedMessage}",
                    Toast.LENGTH_LONG).show()
            }

        }
    }

    lifecycleOwner?.lifecycleScope?.launch {
        pagingData.collectLatest {
            adapter.submitData(it)
        }
    }

    lifecycleOwner?.lifecycleScope?.launch {
        adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
            .filter { it.refresh is LoadState.NotLoading }
            .collectLatest { list.scrollToPosition(0) }
    }
}