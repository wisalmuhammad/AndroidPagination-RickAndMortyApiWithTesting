package com.wisal.android.data.paging

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.wisal.android.service.FakeApiServiceData
import com.wisal.android.viewmodels.UiModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class PagingDataTransformTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    private val testScope = TestScope()
    private val dispatcher = StandardTestDispatcher(testScope.testScheduler)

    @Inject lateinit var fakeData: FakeApiServiceData

    @Before
    fun setup() {
        hiltRule.inject()
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun clean() {
        Dispatchers.resetMain()
    }

    @Test
    fun transformCharacterToUiModel() = testScope.runTest {
        val data  = PagingData.from(fakeData.fakeCharactersOne)
        val transformData: PagingData<UiModel> = data.map { UiModel.CharacterItem(it) }

        val differ = AsyncPagingDataDiffer(
            diffCallback = CharacterDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(transformData)
        advanceUntilIdle()
        assertEquals(
            fakeData.fakeCharactersOne.map {
                UiModel.CharacterItem(it)
            },
            differ.snapshot().items
        )
    }
}

class CharacterDiffCallback : DiffUtil.ItemCallback<UiModel>() {
    override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return (oldItem is UiModel.CharacterItem && newItem is UiModel.CharacterItem
                && oldItem.item.id == newItem.item.id && oldItem.item.name == newItem.item.name)
    }

    override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return oldItem == newItem
    }

}

class NoopListCallback: ListUpdateCallback {
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
}