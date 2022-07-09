package com.bootcamp.soccernews.data.repository

import androidx.lifecycle.LiveData
import com.bootcamp.soccernews.data.model.News
import com.bootcamp.soccernews.data.model.NewsResponse
import retrofit2.Response

interface NewsRepositoryInterface {

    suspend fun getSoccerNews(pageNumber: Int): Response<NewsResponse>

    suspend fun getGeneralNewsBrasil(pageNumber: Int): Response<NewsResponse>

    suspend fun searchForNews(searchQuery: String, pageNumber: Int): Response<NewsResponse>

    suspend fun upsertNewsDataBase(news: News)

    suspend fun deleteNewsDataBase(news: News)

    fun getNewsDataBase(): LiveData<List<News>>

}