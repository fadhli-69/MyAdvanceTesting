package com.example.newsapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.newsapp.BuildConfig
import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.local.room.NewsDao
import com.example.newsapp.data.remote.retrofit.ApiService
import com.example.newsapp.utils.wrapEspressoIdlingResource

class NewsRepository(
    private val apiService: ApiService,
    private val newsDao: NewsDao
) {
    fun getHeadlineNews(): LiveData<Result<List<NewsEntity>>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = apiService.getNews(BuildConfig.API_KEY)
                if (response.articles.isEmpty()) {
                    emit(Result.Error("No news available"))
                    return@wrapEspressoIdlingResource
                }
                val newsList = response.articles.map { article ->
                    NewsEntity(
                        article.title,
                        article.publishedAt,
                        article.urlToImage,
                        article.url
                    )
                }
                emit(Result.Success(newsList))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun getBookmarkedNews(): LiveData<List<NewsEntity>> {
        return newsDao.getBookmarkedNews()
    }

    suspend fun saveNews(news: NewsEntity) {
        newsDao.saveNews(news)
    }

    suspend fun deleteNews(title: String) {
        newsDao.deleteNews(title)
    }

    fun isNewsBookmarked(title: String): LiveData<Boolean> {
        return newsDao.isNewsBookmarked(title)
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            apiService: ApiService,
            newsDao: NewsDao
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService, newsDao)
            }.also { instance = it }
    }
}
