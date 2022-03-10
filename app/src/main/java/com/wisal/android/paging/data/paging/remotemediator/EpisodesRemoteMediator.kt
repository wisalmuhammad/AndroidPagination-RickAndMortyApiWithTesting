package com.wisal.android.paging.data.paging.remotemediator

import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.wisal.android.paging.data.db.EpisodeDatabase
import com.wisal.android.paging.data.service.ApiService
import com.wisal.android.paging.models.Episode
import com.wisal.android.paging.models.EpisodePageKeys
import retrofit2.HttpException
import java.io.IOException

private const val API_STARTING_PAGE_INDEX = 1


@OptIn(ExperimentalPagingApi::class)
class EpisodesRemoteMediator(
    private val apiService: ApiService,
    private val database: EpisodeDatabase
): RemoteMediator<Int,Episode>() {


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Episode>
    ): MediatorResult {
        val pageNumber = when(loadType) {
            LoadType.REFRESH -> {
                val remoteKeys: EpisodePageKeys? = getRemoteKeyClosestToCurrentPosition(state)
                val pageKey = if(remoteKeys?.nextPageUrl != null) {
                    val key = Uri.parse(remoteKeys.nextPageUrl)?.getQueryParameter("page")?.toInt()
                    key
                } else API_STARTING_PAGE_INDEX
                pageKey
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys: EpisodePageKeys? = getRemoteKeyForLastItem(state)
                if(remoteKeys?.nextPageUrl == null) {
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }
                val uri = Uri.parse(remoteKeys.nextPageUrl)
                val nextPageQuery = uri.getQueryParameter("page")
                nextPageQuery?.toInt()
            }
        }

        try {
            val apiResponse = apiService.getAllEpisodes(pageNumber ?: 1)
            val pagedInfo = apiResponse.pageInfo
            val episodes = apiResponse.results

            database.withTransaction {
                if(loadType == LoadType.REFRESH) {
                    with(database) {
                        pageKeysDao().deleteAll()
                        episodesDao().deleteAll()
                    }
                }
                if(episodes.isNotEmpty()) {
                    val episodesPageKeys = episodes.map {
                        EpisodePageKeys(
                            id = it.id,
                            nextPageUrl = pagedInfo.next,
                        )
                    }
                    with(database) {
                        pageKeysDao().insertAllPagesKeys(episodesPageKeys)
                        episodesDao().insertAll(episodes)
                    }
                }
            }

            return MediatorResult.Success(endOfPaginationReached = pagedInfo.next == null)

        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Episode>): EpisodePageKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.pageKeysDao().getPageKeyById(id)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Episode>): EpisodePageKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let {  episode ->
                database.pageKeysDao().getPageKeyById(episode.id)
            }
    }
}