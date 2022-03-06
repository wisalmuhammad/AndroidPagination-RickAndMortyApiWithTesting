package com.wisal.android.service

import com.wisal.android.models.*

class FakeApiServiceData {

    val fakeCharactersOne = listOf(
        Character(id = 0, name = "Jerry", status = Status.ALIVE,"human",type = "type", gender = Gender.FEMALE, image = "imageUrl", origin = NameUrl("name","url"), episode = emptyList(), created = "today", location = NameUrl("name","url"), url = "url"),
        Character(id = 1, name = "Maria", status = Status.ALIVE,"human",type = "type", gender = Gender.FEMALE, image = "imageUrl", origin = NameUrl("name","url"), episode = emptyList(), created = "today", location = NameUrl("name","url"), url = "url"),
    )
    private val fakeCharactersTwo = listOf(
        Character(id = 2, name = "Jerry Mick", status = Status.DEAD,"human",type = "type", gender = Gender.MALE, image = "imageUrl", origin = NameUrl("name","url"), episode = emptyList(), created = "today", location = NameUrl("name","url"), url = "url"),
        Character(id = 3, name = "Jerry", status = Status.DEAD,"human",type = "type", gender = Gender.MALE, image = "imageUrl", origin = NameUrl("name","url"), episode = emptyList(), created = "today", location = NameUrl("name","url"), url = "url"),
    )
    private val fakeCharactersThree = listOf(
        Character(id = 4, name = "Maria", status = Status.ALIVE,"human",type = "type", gender = Gender.FEMALE, image = "imageUrl", origin = NameUrl("name","url"), episode = emptyList(), created = "today", location = NameUrl("name","url"), url = "url"),
        Character(id = 5, name = "Jerry Micky", status = Status.DEAD,"human",type = "type", gender = Gender.FEMALE, image = "imageUrl", origin = NameUrl("name","url"), episode = emptyList(), created = "today", location = NameUrl("name","url"), url = "url"),
    )

    private val fakePagesInfo = listOf(
        PageInfo(
            count = 2,
            prev = null,
            next = "https://rickandmortyapi.com/api/character/?page=2",
            pages = 3
        ),
        PageInfo(
            count = 2,
            prev = "https://rickandmortyapi.com/api/character/?page=1",
            next = "https://rickandmortyapi.com/api/character/?page=3",
            pages = 3
        ),
        PageInfo(
            count = 2,
            prev = "https://rickandmortyapi.com/api/character/?page=2",
            next = null,
            pages = 3
        )
    )

    val fakedPagedResponses: List<PagedResponse<Character>>
    get() = listOf(
        PagedResponse(pageInfo = fakePagesInfo[0], results = fakeCharactersOne),
        PagedResponse(pageInfo = fakePagesInfo[1], results = fakeCharactersTwo),
        PagedResponse(pageInfo = fakePagesInfo[2], results = fakeCharactersThree)
    )



}