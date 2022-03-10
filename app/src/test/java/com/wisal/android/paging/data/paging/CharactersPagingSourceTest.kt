package com.wisal.android.paging.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Refresh
import androidx.paging.PagingSource.LoadResult.Page
import com.wisal.android.paging.data.paging.datasource.CharactersPagingSource
import com.wisal.android.paging.models.*
import com.wisal.android.paging.service.FakeApiService
import com.wisal.android.paging.service.FakeApiServiceData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import kotlin.test.assertEquals


@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CharactersPagingSourceTest {

    private val fakeApiServiceData = FakeApiServiceData()

    private val fakeApiService: FakeApiService = FakeApiService().apply {
        addCharacters(1, fakeApiServiceData.fakedPagedResponses[0])
        addCharacters(2, fakeApiServiceData.fakedPagedResponses[0])
        addCharacters(3,fakeApiServiceData.fakedPagedResponses[0])
    }

    private lateinit var pagingSource: CharactersPagingSource

    @Before
    fun setup() {
        pagingSource = CharactersPagingSource(fakeApiService)
    }

    @Test
    fun `load return http error When on failure of loading pagedKey data`() = runTest {
        val error = IOException("404 item not found")
        fakeApiService.error = error
        val expectedResult = PagingSource.LoadResult.Error<Int,Character>(error)
        val actual = pagingSource.load(
            Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )
        assertEquals(expected = expectedResult, actual = actual)
    }


    @Test
    fun `load returns page When on successful load of pageKeyed data()`() = runTest {
        val expectedResult: Page<Int,Character> = Page(
            data = fakeApiServiceData.fakedPagedResponses[0].results,
            prevKey = null,
            nextKey = 2
        )
        val actual = pagingSource.load(
            Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        assertEquals(expected = expectedResult, actual = actual)

    }

}