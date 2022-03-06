package com.wisal.android.ui

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
import androidx.paging.PagingData
import com.wisal.android.adapters.CharactersAdapter
import com.wisal.android.viewmodels.CharactersViewModel
import com.wisal.android.viewmodels.UiModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.RecyclerView
import com.wisal.android.adapters.PagingLoadStateAdapter
import com.wisal.android.data.paging.databinding.CharactersFragmentBinding
import com.wisal.android.viewmodels.UiAction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CharactersFragment : Fragment() {

    private val TAG = "CharactersFragment"

    @Inject lateinit var adapter: CharactersAdapter

    private lateinit var binding: CharactersFragmentBinding
    private val viewModel by viewModels<CharactersViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CharactersFragmentBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            this.viewModel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter(adapter),
                footer = PagingLoadStateAdapter(adapter)
        )

        binding.bindList(
            pagingData = viewModel.characters,
            adapter = adapter,
            uiActions = viewModel.accept
        )

    }


}


private fun CharactersFragmentBinding.bindList(
    pagingData: Flow<PagingData<UiModel>>,
    adapter: CharactersAdapter,
    uiActions: (UiAction) -> Unit,
) {

    retryButton.setOnClickListener {
        adapter.retry()
    }

     lifecycleOwner?.lifecycleScope?.launch {

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