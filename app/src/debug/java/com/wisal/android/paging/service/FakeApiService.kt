package com.wisal.android.paging.service

import com.wisal.android.paging.data.service.ApiService
import com.wisal.android.paging.models.*
import java.io.IOException

class FakeApiService: ApiService {

    private val model = mutableMapOf<Int,PagedResponse<Character>>()

    var error: IOException? = null

    fun addCharacters(key: Int,pagedResponse: PagedResponse<Character>) {
        model.putIfAbsent(key,pagedResponse)
    }

    override suspend fun getAllCharacters(page: Int): PagedResponse<Character> {
        error?.let {
            throw it
        }
        return model.getOrDefault(page,PagedResponse(
            pageInfo = PageInfo(0,0,null,null),
            results = emptyList()
        ))
    }

    override suspend fun getAllEpisodes(page: Int): PagedResponse<Episode> {
        TODO("Not yet implemented")
    }

    override suspend fun getLocationItems(page: Int): PagedResponse<Location> {
        TODO("Not yet implemented")
    }
}