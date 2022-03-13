package com.wisal.android.paging.provider

import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.wisal.android.paging.models.Gender
import com.wisal.android.paging.models.NameUrl
import com.wisal.android.paging.models.Status


object RickAndMortyApiContract {

    // The URI Code for All items
    const val ALL_CHARACTER_ITEMS = -2
    //The URI suffix for counting records
    const val CHARACTERS_COUNT = "characters_count"
    //The URI Authority
    const val AUTHORITY = "com.wisal.android.paging.provider"
    // Only one public table.
    const val CHARACTERS_CONTENT_PATH = "characters_items"
    // Content URI for this table. Returns all items.
    val CHARACTERS_CONTENT_URI = Uri.parse(
        "content://$AUTHORITY/$CHARACTERS_CONTENT_PATH"
    )

    // URI to get the number of entries.
    val CHARACTERS_ROW_COUNT_URI = Uri.parse(
        "content://$AUTHORITY/$CHARACTERS_CONTENT_PATH/$CHARACTERS_COUNT"
    )

    // Single record mime type
    const val SINGLE_CHARACTER_MIME_TYPE = "vnd.android.cursor.item/vnd.com.wisal.android.paging.provider.characters_items"
    const val MULTIPLE_CHARACTERS_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.com.wisal.android.paging.provider.characters_items"

    const val CHARACTERS_DATABASE_NAME = "characters_items.db"

    object CharactersTable {

        const val TABLE_NAME = "characters_items"

        object Columns {
            const val KEY_CHARACTER_ID = "id"
            const val KEY_CHARACTER_NAME = "name"
            const val KEY_CHARACTER_STATUS = "status"
            const val KEY_CHARACTER_SPECIES = "species"
            const val KEY_CHARACTER_TYPE = "type"
            const val KEY_CHARACTER_GENDER = "gender"
            const val KEY_CHARACTER_IMAGE = "image"
            const val KEY_CHARACTER_URL = "url"
            const val KEY_CHARACTER_ORIGIN = "origin"
            const val KEY_CHARACTER_LOCATION = "location"
            const val KEY_CHARACTER_EPISODE = "episode"
            const val KEY_CHARACTER_CREATED = "created"
        }
    }


}