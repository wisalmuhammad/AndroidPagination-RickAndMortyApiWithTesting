package com.wisal.android.paging.data.paging.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.wisal.android.paging.R
import com.wisal.android.paging.service.launchFragmentInHiltContainer
import com.wisal.android.paging.ui.CharactersFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


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