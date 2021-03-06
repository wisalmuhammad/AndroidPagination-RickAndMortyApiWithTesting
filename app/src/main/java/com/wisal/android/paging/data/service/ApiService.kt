package com.wisal.android.paging.data.service

import com.wisal.android.paging.models.Character
import com.wisal.android.paging.models.Episode
import com.wisal.android.paging.models.Location
import com.wisal.android.paging.models.PagedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("character/")
    suspend fun getAllCharacters(@Query("page") page: Int): PagedResponse<Character>

    @GET("episode/")
    suspend fun getAllEpisodes(@Query("page")page: Int): PagedResponse<Episode>

    @GET("location/")
    suspend fun getLocationItems(@Query("page")page: Int): PagedResponse<Location>

}