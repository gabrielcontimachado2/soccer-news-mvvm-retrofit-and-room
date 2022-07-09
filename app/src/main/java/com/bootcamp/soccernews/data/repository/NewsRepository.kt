package com.bootcamp.soccernews.data.repository

import com.bootcamp.soccernews.data.api.RetrofitInstance
import com.bootcamp.soccernews.data.database.local.NewsDataBase
import com.bootcamp.soccernews.data.model.News

class NewsRepository(
    val dataBase: NewsDataBase
) {

    suspend fun getSoccerNews(pageNumber: Int) =
        RetrofitInstance.api.getSoccerNews(pageNumber)

    suspend fun getGeneralNewsBrasil(pageNumber: Int) =
        RetrofitInstance.api.getGeneralNewsBrasil(pageNumber)

    suspend fun searchForNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.SearchForNews(searchQuery, pageNumber)

    suspend fun upsertNewsDataBase(news: News) =
        dataBase.getNewsDao().upsert(news)

    fun getNewsDataBase() =
        dataBase.getNewsDao().getAllNews()

    suspend fun deleteNewsDataBase(news: News) =
        dataBase.getNewsDao().deleteNews(news)

}