package com.wisal.android.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.wisal.android.data.repository.Repository
import com.wisal.android.models.Episode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodesViewModel @Inject constructor (
    private val repository: Repository,
) : ViewModel() {

    val episodes: Flow<PagingData<UiModel>>
    val accept: (UiAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<UiAction>()

        episodes = actionStateFlow
            .filterIsInstance<UiAction.Query>()
            .distinctUntilChanged()
            .onStart {
                emit(UiAction.Query("episode"))
            }.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            ).flatMapLatest {
                getRemoteItems(it.query)
            }.cachedIn(viewModelScope)

        accept = { uiAction ->
            viewModelScope.launch {
                actionStateFlow.emit(uiAction)
            }
        }

    }

    private fun getRemoteItems(query: String?): Flow<PagingData<UiModel>> {
        return repository.getAllEpisodes()
            .map { pagingData ->
                pagingData.map { episode ->
                    UiModel.EpisodeItem(episode)
                }
            }
            .map {
                it.insertSeparators { before ,after ->
                    when {
                        before == null -> UiModel.SeparatorModel(
                            "BEFORE ITEMS SEPARATOR"
                        )
                        after == null -> UiModel.SeparatorModel(
                            "AFTER ITEMS SEPARATOR"
                        )
                        else -> null
                    }
                }
            }
    }
}