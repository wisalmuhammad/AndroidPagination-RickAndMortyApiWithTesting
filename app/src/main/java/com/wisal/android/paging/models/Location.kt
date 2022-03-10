package com.wisal.android.paging.models


data class Location(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residence: List<String>?,
    val url: String,
    val created: String
)