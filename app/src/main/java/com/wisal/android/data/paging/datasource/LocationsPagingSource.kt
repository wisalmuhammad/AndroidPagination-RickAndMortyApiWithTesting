package com.wisal.android.data.paging.datasource

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wisal.android.data.service.ApiService
import com.wisal.android.models.Character
import com.wisal.android.models.Location
import java.io.IOException

private const val API_STARTING_PAGE_INDEX = 1

class LocationsPagingSource(
    private val apiService: ApiService
): PagingSource<Int,Location>() {

    override fun getRefreshKey(state: PagingState<Int, Location>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Location> {
        val pageNumber = params.key ?: API_STARTING_PAGE_INDEX

        return try {
            val apiResponse = apiService.getLocationItems(pageNumber)
            val pagedInfo = apiResponse.pageInfo
            val locations = apiResponse.results
            var nextKey: Int?
            pagedInfo.next?.let {
                val uri = Uri.parse(apiResponse.pageInfo.next)
                nextKey = uri.getQueryParameter("page")?.toInt()
                nextKey
            }
            var prevKey: Int?
            pagedInfo.prev?.let {
                val uri = Uri.parse(apiResponse.pageInfo.prev)
                prevKey = uri.getQueryParameter("page")?.toInt()
                prevKey
            }

            LoadResult.Page(
                data = locations.orEmpty(),
                prevKey = null,
                nextKey = null
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}