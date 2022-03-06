package com.wisal.android.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wisal.android.data.db.CharacterDatabase
import com.wisal.android.data.db.EpisodeDatabase
import com.wisal.android.data.paging.datasource.LocationsPagingSource
import com.wisal.android.data.paging.remotemediator.CharacterRemoteMediator
import com.wisal.android.data.paging.remotemediator.EpisodesRemoteMediator
import com.wisal.android.data.service.ApiService
import com.wisal.android.models.Character
import com.wisal.android.models.Episode
import com.wisal.android.models.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface Repository {
    fun getAllCharacters(): Flow<PagingData<Character>>
    fun getAllEpisodes(): Flow<PagingData<Episode>>
    fun getAllLocationItems(): Flow<PagingData<Location>>
}

@OptIn(ExperimentalPagingApi::class)
class RepositoryImp @Inject constructor(
    private val apiService: ApiService,
    private val characterDatabase: CharacterDatabase,
    private val episodesDatabase: EpisodeDatabase
): Repository {

   override fun getAllCharacters(): Flow<PagingData<Character>> {
       val pagingSourceFactory = {
           characterDatabase.charactersDao().getAllCharacters()
       }
       return Pager(
           config = PagingConfig(
               pageSize = NETWORK_PAGE_SIZE,
               enablePlaceholders = false,
               prefetchDistance = 2
           ),

           remoteMediator = CharacterRemoteMediator(
               apiService = apiService,
               database = characterDatabase
           ),
           pagingSourceFactory = pagingSourceFactory

       ).flow.flowOn(Dispatchers.IO)
   }

    override fun getAllEpisodes(): Flow<PagingData<Episode>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                prefetchDistance = 2
            ),
            remoteMediator = EpisodesRemoteMediator(
                apiService = apiService,
                database = episodesDatabase
            ),
            pagingSourceFactory = {
                Log.d("Debug","Paging source invalidated")
                episodesDatabase.episodesDao().pagingSource()
            }
        ).flow.flowOn(Dispatchers.IO)
    }

    override fun getAllLocationItems(): Flow<PagingData<Location>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            pagingSourceFactory = {
                LocationsPagingSource(apiService = apiService)
            }
        ).flow.flowOn(Dispatchers.IO)
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }


}