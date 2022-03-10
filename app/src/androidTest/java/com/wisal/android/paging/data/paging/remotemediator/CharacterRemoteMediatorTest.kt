package com.wisal.android.paging.data.paging.remotemediator

import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.paging.*
import com.wisal.android.paging.data.db.CharacterDatabase
import com.wisal.android.paging.models.Character
import com.wisal.android.paging.service.FakeApiService
import com.wisal.android.paging.service.FakeApiServiceData
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

@ExperimentalPagingApi
@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class CharacterRemoteMediatorTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("characters_test_db")
    lateinit var database: CharacterDatabase
    @Inject lateinit var apiService: FakeApiService
    @Inject lateinit var fakeApiServiceData: FakeApiServiceData

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        database.clearAllTables()
        database.close()
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = CharacterRemoteMediator(
            apiService = apiService,
            database = database
        )
        val pagingState = PagingState<Int,Character> (
            pages = listOf(),
            anchorPosition = null,
            PagingConfig(2),
            leadingPlaceholderCount = 2
        )

        val result = remoteMediator.load(
            LoadType.REFRESH,
            pagingState
        )

        assertTrue((result is RemoteMediator.MediatorResult.Success))
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

    }

    @Test
    fun refreshLoadSuccessAndEndOfPaginationWhenNoMoreData() = runTest {
        // To test endOfPaginationReached, don't set up the mockApi to return post
        // data here.
        val remoteMediator = CharacterRemoteMediator(
            apiService = apiService,
            database = database
        )
        val pagingState = PagingState<Int,Character> (
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = fakeApiServiceData.fakedPagedResponses[2].results,
                    prevKey = 2,
                    nextKey = fakeApiServiceData.fakedPagedResponses[2].pageInfo.next?.toInt()
                )
            ),
            anchorPosition = null,
            PagingConfig(2),
            leadingPlaceholderCount = 2
        )
        val result = remoteMediator.load(LoadType.APPEND, pagingState)
        assertTrue((result is RemoteMediator.MediatorResult.Success))
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun refreshLoadReturnsErrorResultWhenErrorOccurs() = runTest {
        val error = IOException("Throw test failure")
        apiService.error = error

        val remoteMediator = CharacterRemoteMediator(
            apiService = apiService,
            database = database
        )
        val pagingState = PagingState<Int, Character>(
            listOf(),
            null,
            PagingConfig(2),
            2
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue ((result is RemoteMediator.MediatorResult.Error))
    }

}