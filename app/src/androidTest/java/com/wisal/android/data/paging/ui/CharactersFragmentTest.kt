package com.wisal.android.data.paging.ui

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.wisal.android.data.db.CharacterDatabase
import com.wisal.android.data.db.EpisodeDatabase
import com.wisal.android.data.paging.R
import com.wisal.android.service.launchFragmentInHiltContainer
import com.wisal.android.service.FakeApiServiceData
import com.wisal.android.ui.CharactersFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import javax.inject.Inject
import javax.inject.Named


@HiltAndroidTest
class CharactersFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun clear() {

    }

    @Test
    fun loadsTheDefaultResults() {
        launchFragmentInHiltContainer<CharactersFragment>()
        onView(withId(R.id.list)).check { view, noViewFoundException ->
            if(noViewFoundException != null) {
                throw noViewFoundException
            }
            val recyclerView = view as RecyclerView
            assertEquals(2,recyclerView.adapter?.itemCount)
        }
    }

}