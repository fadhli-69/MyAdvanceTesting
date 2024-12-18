package com.example.newsapp.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.newsapp.data.NewsRepository
import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.utils.DataDummy
import org.junit.Test
import org.junit.Assert
import org.junit.Before
import org.mockito.Mockito.`when`
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.junit.runner.RunWith
import com.example.newsapp.data.Result
import com.example.newsapp.utils.getOrAwaitValue
import org.junit.Rule
import org.mockito.Mockito

// Latihan Unit Testing LiveData
@RunWith(MockitoJUnitRunner::class)
class NewsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var newsRepository: NewsRepository
    private lateinit var newsViewModel: NewsViewModel
    private val dummyNews = DataDummy.generateDummyNewsEntity()

    @Before
    fun setUp() {
        newsViewModel = NewsViewModel(newsRepository)
    }

    @Test
    fun `when Get HeadlineNews Should Not Null and Return Success`() {
        val expectedNews = MutableLiveData<Result<List<NewsEntity>>>()
        expectedNews.value = Result.Success(dummyNews)

        `when`(newsRepository.getHeadlineNews()).thenReturn(expectedNews)

        val actualNews = newsViewModel.getHeadlineNews().getOrAwaitValue()
        Mockito.verify(newsRepository).getHeadlineNews()
        Assert.assertNotNull(actualNews)
        Assert.assertTrue(actualNews is Result.Success)
        Assert.assertEquals(dummyNews.size, (actualNews as Result.Success).data.size)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val headlineNews = MutableLiveData<Result<List<NewsEntity>>>()
        headlineNews.value = Result.Error("Error")
        `when`(newsRepository.getHeadlineNews()).thenReturn(headlineNews)
        val actualNews = newsViewModel.getHeadlineNews().getOrAwaitValue()
        Mockito.verify(newsRepository).getHeadlineNews()
        Assert.assertNotNull(actualNews)
        Assert.assertTrue(actualNews is Result.Error)
    }

    @Test
    fun `when Get BookmarkedNews Should Not Null and Return Data`() {
        val dummyBookmarkedNews = DataDummy.generateDummyNewsEntity()
        val expectedLiveData = MutableLiveData<List<NewsEntity>>()
        expectedLiveData.value = dummyBookmarkedNews

        `when`(newsRepository.getBookmarkedNews()).thenReturn(expectedLiveData)

        val actualBookmarkedNews = newsViewModel.getBookmarkedNews().getOrAwaitValue()

        Mockito.verify(newsRepository).getBookmarkedNews()

        Assert.assertNotNull(actualBookmarkedNews)
        Assert.assertEquals(dummyBookmarkedNews.size, actualBookmarkedNews.size)
        Assert.assertEquals(dummyBookmarkedNews, actualBookmarkedNews)
    }


    //boilerplate without Utils
    /*@Test
    fun `when Get HeadlineNews Should Not Null and Return Success`() {
        val expectedNews = MutableLiveData<Result<List<NewsEntity>>>()
        expectedNews.value = Result.Success(dummyNews)

        `when`(newsRepository.getHeadlineNews()).thenReturn(expectedNews)

        val actualNews = newsViewModel.getHeadlineNews().getOrAwaitValue()
        Mockito.verify(newsRepository).getHeadlineNews()
        Assert.assertNotNull(actualNews)
        Assert.assertTrue(actualNews is Result.Success)
        Assert.assertEquals(dummyNews.size, (actualNews as Result.Success).data.size)
    }*/
}
