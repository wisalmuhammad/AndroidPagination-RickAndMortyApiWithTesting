package com.wisal.android.data.paging.remotemediator

import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.wisal.android.data.db.CharacterDatabase
import com.wisal.android.data.service.ApiService
import com.wisal.android.models.Character
import com.wisal.android.models.CharacterPageKeys
import retrofit2.HttpException
import java.io.IOException

private const val API_STARTING_PAGE_INDEX = 1


@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator (
    private val apiService: ApiService,
    private val database: CharacterDatabase
): RemoteMediator<Int,Character>() {


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Character>
    ): MediatorResult {

        val pageNumber = when(loadType) {
            LoadType.REFRESH -> {
                val remoteKeys: CharacterPageKeys? = getRemoteKeyClosestToCurrentPosition(state)
                val pageKey = if(remoteKeys?.nextPageUrl != null) {
                    val key = Uri.parse(remoteKeys.nextPageUrl)?.getQueryParameter("page")?.toInt()
                    key
                } else API_STARTING_PAGE_INDEX
                pageKey
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys: CharacterPageKeys? = getRemoteKeyForLastItem(state)
                if(remoteKeys?.nextPageUrl == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                val uri = Uri.parse(remoteKeys.nextPageUrl)
                val nextPageQuery = uri.getQueryParameter("page")
                nextPageQuery?.toInt()
            }
        }

        try {
            val apiResponse = apiService.getAllCharacters(pageNumber ?: API_STARTING_PAGE_INDEX)
            val pagesInfo = apiResponse.pageInfo
            val characters = apiResponse.results
            database.withTransaction {
                if(loadType == LoadType.REFRESH) {
                    with(database) {
                        remoteKeysDao().clearRemoteKeys()
                        charactersDao().clearAllCharacters()
                    }
                }

                val keys = characters.map {
                    CharacterPageKeys(id = it.id,
                        nextPageUrl = pagesInfo.next
                    )
                }
                with(database) {
                    remoteKeysDao().insertAll(keys)
                    charactersDao().insertAll(characters)
                }
            }

            return MediatorResult.Success(endOfPaginationReached = pagesInfo.next == null)

        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }

    }


    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Character>): CharacterPageKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().remoteKeysCharacterId(id)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Character>): CharacterPageKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let {  character ->
                database.remoteKeysDao().remoteKeysCharacterId(character.id)
            }
    }

}