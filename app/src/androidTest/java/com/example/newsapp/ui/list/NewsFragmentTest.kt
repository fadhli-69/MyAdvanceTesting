package com.example.newsapp.ui.list

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.IdlingRegistry
import com.JsonConverter
import com.example.newsapp.R
import com.example.newsapp.data.remote.retrofit.ApiConfig
import com.example.newsapp.utils.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Latihan Integration Testing Fragment dengan MockWebServer dan Idling Resource
@RunWith(AndroidJUnit4::class)
class NewsFragmentTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        // Register Espresso Idling Resource
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        // Start MockWebServer
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
    }

    @After
    fun tearDown() {
        // Unregister Espresso Idling Resource
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)

        // Shutdown MockWebServer
        mockWebServer.shutdown()
    }

    @Test
    fun getHeadlineNews_Success() {
        val bundle = Bundle()
        bundle.putString(NewsFragment.ARG_TAB, NewsFragment.TAB_NEWS)
        launchFragmentInContainer<NewsFragment>(bundle, R.style.Theme_News)

        // Mock successful response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)

        // Assertions
        onView(withId(R.id.rv_news))
            .check(matches(isDisplayed()))
        onView(withText("Nvidia partners leak next-gen RTX 50-series GPUs, including a 32GB 5090 - Ars Technica"))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rv_news))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText("Teacher and student killed in Wisconsin school shooting, female suspect also dead: Here’s what we know so far - Yahoo! Voices"))
                )
            )
    }

    @Test
    fun getHeadlineNews_Error() {
        val bundle = Bundle()
        bundle.putString(NewsFragment.ARG_TAB, NewsFragment.TAB_NEWS)
        launchFragmentInContainer<NewsFragment>(bundle, R.style.Theme_News)

        // Mock error response
        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        // Assertions
        onView(withId(R.id.tv_error))
            .check(matches(isDisplayed()))
        onView(withText("Oops.. something went wrong."))
            .check(matches(isDisplayed()))
    }
}
