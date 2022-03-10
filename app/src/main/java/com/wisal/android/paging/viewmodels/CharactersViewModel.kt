package com.wisal.android.paging.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.wisal.android.paging.data.repository.Repository
import com.wisal.android.paging.models.Character
import com.wisal.android.paging.models.Episode
import com.wisal.android.paging.models.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CharactersViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val TAG = "CharactersViewModel"

    val characters: Flow<PagingData<UiModel>>
    val accept: (UiAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<UiAction>()
        characters = actionStateFlow
            .filterIsInstance<UiAction.Query>()
            .distinctUntilChanged()
            .onStart {
                emit(UiAction.Query())
            }.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            ).flatMapLatest {
                Log.d(TAG,"Ui action query: ${it.query}")
                searchCharacters(it.query)
            }.cachedIn(viewModelScope)

        accept = { uiAction ->
            viewModelScope.launch {
                actionStateFlow.emit(uiAction)
            }
        }

    }

    private fun searchCharacters(queryString: String?): Flow<PagingData<UiModel>> {
        return repository.getAllCharacters()
            .map {
                    pagingData -> pagingData.map { UiModel.CharacterItem(it) }
            }
            .map {
                it.insertSeparators { _,_ ->
                    null
                }
            }

    }

}


sealed class UiAction {
    data class Refresh(val status: Boolean = false): UiAction()
    data class Query(val query: String? = null): UiAction()
}

sealed class UiModel {
    data class CharacterItem(val item: Character): UiModel()
    data class EpisodeItem(val episode: Episode): UiModel()
    data class LocationItem(val location: Location): UiModel()
    data class SeparatorModel(val description: String) : UiModel()
}
