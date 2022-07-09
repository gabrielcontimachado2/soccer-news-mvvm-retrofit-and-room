package com.bootcamp.soccernews.data.repository

import androidx.lifecycle.LiveData
import com.bootcamp.soccernews.data.api.RetrofitInstance
import com.bootcamp.soccernews.data.database.local.NewsDataBase
import com.bootcamp.soccernews.data.model.News
import com.bootcamp.soccernews.data.model.NewsResponse
import retrofit2.Response


class NewsRepository(
    val dataBase: NewsDataBase
) : NewsRepositoryInterface {


    override suspend fun getGeneralNewsBrasil(pageNumber: Int): Response<NewsResponse> {
        return RetrofitInstance.api.getGeneralNewsBrasil(pageNumber)
    }

    override suspend fun searchForNews(
        searchQuery: String,
        pageNumber: Int
    ): Response<NewsResponse> {
        return RetrofitInstance.api.SearchForNews(searchQuery, pageNumber)
    }

    override suspend fun getSoccerNews(pageNumber: Int): Response<NewsResponse> {
        return RetrofitInstance.api.getSoccerNews(pageNumber)
    }

    override suspend fun upsertNewsDataBase(news: News) {
        dataBase.getNewsDao().upsert(news)
    }

    override suspend fun deleteNewsDataBase(news: News) {
        dataBase.getNewsDao().deleteNews(news)
    }

    override fun getNewsDataBase(): LiveData<List<News>> {
        return dataBase.getNewsDao().getAllNews()
    }


}