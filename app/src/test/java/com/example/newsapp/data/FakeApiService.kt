package com.example.newsapp.data

import com.example.newsapp.data.remote.response.NewsResponse
import com.example.newsapp.data.remote.retrofit.ApiService
import com.example.newsapp.utils.DataDummy

class FakeApiService : ApiService {
    private val dummyResponse = DataDummy.generateDummyNewsResponse()
    override suspend fun getNews(apiKey: String): NewsResponse {
        return dummyResponse
    }
}