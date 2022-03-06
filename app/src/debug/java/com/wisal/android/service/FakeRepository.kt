package com.wisal.android.service

import androidx.paging.PagingData
import com.wisal.android.data.repository.Repository
import com.wisal.android.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FakeRepository @Inject constructor(): Repository {
    private val characters: List<Character> = listOf(
        Character(id = 0, name = "Jerry", status = Status.ALIVE,"human",type = "type", gender = Gender.FEMALE, image = "imageUrl", origin = NameUrl("name","url"), episode = emptyList(), created = "today", location = NameUrl("name","url"), url = "url"),
        Character(id = 1, name = "Maria", status = Status.ALIVE,"human",type = "type", gender = Gender.FEMALE, image = "imageUrl", origin = NameUrl("name","url"), episode = emptyList(), created = "today", location = NameUrl("name","url"), url = "url"),
    )

    override fun getAllCharacters(): Flow<PagingData<Character>> {
        return flowOf(PagingData.from(
            characters
        ))
    }

    override fun getAllEpisodes(): Flow<PagingData<Episode>> {
        TODO("Not yet implemented")
    }

    override fun getAllLocationItems(): Flow<PagingData<Location>> {
        TODO("Not yet implemented")
    }
}