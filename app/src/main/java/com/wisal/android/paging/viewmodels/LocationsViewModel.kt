package com.wisal.android.paging.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.wisal.android.paging.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    val locations: Flow<PagingData<UiModel>>
    val accept: (UiAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<UiAction>()
        locations = actionStateFlow
            .filterIsInstance<UiAction.Query>()
            .distinctUntilChanged()
            .onStart {
                emit(UiAction.Query("location"))
            }.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            ).flatMapLatest {
                getLocationsItemData(it.query)
            }.cachedIn(viewModelScope)

        accept = { uiAction ->
            viewModelScope.launch {
                actionStateFlow.emit(uiAction)
            }
        }
    }

    private fun getLocationsItemData(query: String?): Flow<PagingData<UiModel>> {
        return repository.getAllLocationItems()
            .map { pagingData ->
                pagingData.map { location ->
                    UiModel.LocationItem(location)
                }
            }
            .map {
                it.insertSeparators { before ,after ->
                    when {
                        before != null && after != null -> UiModel.SeparatorModel("SEPARATOR BETWEEN ITEMS")
                        else -> null
                    }
                }
            }
    }

}