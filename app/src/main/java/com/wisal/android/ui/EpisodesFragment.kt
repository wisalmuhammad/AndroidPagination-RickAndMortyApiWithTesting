package com.wisal.android.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
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
import androidx.paging.map
import com.wisal.android.adapters.EpisodesAdapter
import com.wisal.android.adapters.PagingLoadStateAdapter
import com.wisal.android.data.paging.R
import com.wisal.android.data.paging.databinding.EpisodesFragmentBinding
import com.wisal.android.viewmodels.EpisodesViewModel
import com.wisal.android.viewmodels.UiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class EpisodesFragment : Fragment() {

    @Inject lateinit var episodesAdapter: EpisodesAdapter

    private lateinit var binding: EpisodesFragmentBinding
    private val viewModel: EpisodesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EpisodesFragmentBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            list.adapter = episodesAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter(episodesAdapter)
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindList(
            pagingData = viewModel.episodes,
            adapter = episodesAdapter
        )
    }


}

private fun EpisodesFragmentBinding.bindList(
    pagingData: Flow<PagingData<UiModel>>,
    adapter: EpisodesAdapter
) {
    retryButton.setOnClickListener {
        adapter.retry()
    }

    lifecycleOwner?.lifecycleScope?.launchWhenCreated {
        adapter.loadStateFlow.collect { loadState ->
            list.isVisible = loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
            progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0

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

    lifecycleOwner?.lifecycleScope?.launchWhenCreated {
        pagingData.collectLatest {
            adapter.submitData(it)
        }
    }

    lifecycleOwner?.lifecycleScope?.launchWhenCreated {
        adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
            .filter { it.refresh is LoadState.NotLoading }
            .collectLatest { list.scrollToPosition(0) }
    }

}