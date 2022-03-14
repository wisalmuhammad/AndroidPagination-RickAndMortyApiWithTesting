package com.wisal.android.paging.provider

import android.content.*
import android.database.Cursor
import android.net.Uri
import com.wisal.android.paging.data.db.CharacterDatabase
import com.wisal.android.paging.models.Character
import com.wisal.android.paging.models.CharacterPageKeys
import com.wisal.android.paging.provider.RickAndMortyApiContract.AUTHORITY
import com.wisal.android.paging.provider.RickAndMortyApiContract.CHARACTERS_CONTENT_PATH
import com.wisal.android.paging.provider.RickAndMortyApiContract.CHARACTERS_COUNT
import com.wisal.android.paging.provider.RickAndMortyApiContract.MULTIPLE_CHARACTERS_RECORDS_MIME_TYPE
import com.wisal.android.paging.provider.RickAndMortyApiContract.SINGLE_CHARACTER_MIME_TYPE
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.lang.UnsupportedOperationException


class RickAndMortyApiContentProvider : ContentProvider() {

    private val TAG = "R&MApiContentProvider"

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface RickAndMortyApiContentProviderEntryPoint {
        fun provideCharactersDatabase(): CharacterDatabase
    }

    private lateinit var sUriMatcher : UriMatcher

    private val URI_CHARACTERS_ALL_ITEMS_CODE = 10
    private val URI_CHARACTER_ONE_ITEM_CODE = 11
    private val URI_CHARACTER_COUNT_CODE = 12

    private fun getCharactersDatabase(appContext: Context): CharacterDatabase {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext,
            RickAndMortyApiContentProviderEntryPoint::class.java
        )
        return hiltEntryPoint.provideCharactersDatabase()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return when (sUriMatcher.match(uri)) {
            URI_CHARACTERS_ALL_ITEMS_CODE -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            URI_CHARACTER_ONE_ITEM_CODE -> {
                val appContext = context?.applicationContext ?: throw IllegalStateException()
                val database = getCharactersDatabase(appContext)
                val charaDao = database.charactersDao()
                val count: Int = charaDao.deleteById(ContentUris.parseId(uri).toString())
                val keyDao = database.remoteKeysDao().deleteById(ContentUris.parseId(uri).toString())
                appContext.contentResolver.notifyChange(uri, null)
                count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when(sUriMatcher.match(uri)) {
            URI_CHARACTER_ONE_ITEM_CODE -> SINGLE_CHARACTER_MIME_TYPE
            URI_CHARACTERS_ALL_ITEMS_CODE -> MULTIPLE_CHARACTERS_RECORDS_MIME_TYPE
            else -> throw IllegalArgumentException("Unknown Uri get type: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun onCreate(): Boolean {
        initUriMatcher()
        return true
    }

    private fun initUriMatcher() {
        sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        sUriMatcher.addURI(AUTHORITY,CHARACTERS_CONTENT_PATH,URI_CHARACTERS_ALL_ITEMS_CODE)
        sUriMatcher.addURI(AUTHORITY, "$CHARACTERS_CONTENT_PATH/#",URI_CHARACTER_ONE_ITEM_CODE)
        sUriMatcher.addURI(AUTHORITY, "$CHARACTERS_CONTENT_PATH/$CHARACTERS_COUNT",URI_CHARACTER_COUNT_CODE)
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        var cursor: Cursor? = null
        when(sUriMatcher.match(uri)) {
            URI_CHARACTER_ONE_ITEM_CODE -> {
                val appContext = context?.applicationContext ?: throw IllegalStateException()
                cursor = getCharactersDatabase(appContext)
                    .charactersDao()
                    .getCharacterByIdCursor(uri.lastPathSegment?.toInt().toString())
            }
            URI_CHARACTER_COUNT_CODE -> {
                val appContext = context?.applicationContext ?: throw IllegalStateException()
                cursor = getCharactersDatabase(appContext)
                    .charactersDao()
                    .getAllCharactersCount()
            }
            URI_CHARACTERS_ALL_ITEMS_CODE -> {
                val appContext = context?.applicationContext ?: throw IllegalStateException()
                cursor = getCharactersDatabase(appContext).charactersDao().getAllCharactersCursor()
            }
            UriMatcher.NO_MATCH -> throw IllegalArgumentException("Invalid uri")
            else -> throw IllegalArgumentException("Invalid uri")
        }
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException()
    }
}