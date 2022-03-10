package com.wisal.android.paging.data.paging.datasource

import android.net.Uri
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wisal.android.paging.data.service.ApiService
import com.wisal.android.paging.models.Episode
import java.io.IOException

private const val RICK_MORTY_DEFAULT_API_INDEX = 1

class EpisodesPagingSource (private val apiService: ApiService)
    : PagingSource<Int,Episode>() {

    override fun getRefreshKey(state: PagingState<Int, Episode>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Episode> {
        Log.d("Debug","Episode paging source load called")
        val position = params.key ?: RICK_MORTY_DEFAULT_API_INDEX

        return try {
            val apiResponse = apiService.getAllEpisodes(position)
            val result = apiResponse.results
            val pagedInfo = apiResponse.pageInfo
            val nextKey: Int? = if (pagedInfo.next != null) {
                val key = Uri.parse(pagedInfo.next).getQueryParameter("page")?.toInt()
                key
            } else null

            val prevKey: Int? = if (pagedInfo.prev != null) {
                val key = Uri.parse(pagedInfo.prev).getQueryParameter("page")?.toInt()
                key
            } else null


            LoadResult.Page(
                data = result.orEmpty(),
                prevKey = prevKey,
                nextKey = nextKey
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}