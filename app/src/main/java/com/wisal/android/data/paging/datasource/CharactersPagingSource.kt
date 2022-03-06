package com.wisal.android.data.paging.datasource

import android.net.Uri
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wisal.android.data.service.ApiService
import com.wisal.android.models.Character
import okio.IOException
import javax.inject.Inject

private const val API_STARTING_PAGE_INDEX = 1

class CharactersPagingSource @Inject constructor(private val apiService: ApiService): PagingSource<Int,Character>()  {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {

        val position = params.key ?: API_STARTING_PAGE_INDEX

        return try {
            val apiResponse = apiService.getAllCharacters(position)
            val characters = apiResponse.results

            var nextKey: Int? = null
            apiResponse.pageInfo.next?.let {
                val uri = Uri.parse(apiResponse.pageInfo.next)
                nextKey = uri?.getQueryParameter("page")?.toInt()
                nextKey
            }
            var prevKey: Int? = null
            apiResponse.pageInfo.prev?.let {
                val uri = Uri.parse(apiResponse.pageInfo.prev)
                prevKey = uri?.getQueryParameter("page")?.toInt()
                prevKey
            }

            LoadResult.Page(
                data = characters.orEmpty(),
                nextKey = nextKey,
                prevKey = prevKey
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }

    }

}