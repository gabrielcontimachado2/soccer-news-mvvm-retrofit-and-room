package com.bootcamp.soccernews.data.repository

import com.bootcamp.soccernews.data.api.RetrofitInstance

class NewsRepository(
    //val dataBase: NewsDataBase
) {

    suspend fun getFootballNews() =
        RetrofitInstance.api.getFootballNews()

    suspend fun getGeneralNewsBrasil() =
        RetrofitInstance.api.getGeneralNewsBrasil()

    suspend fun searchForNews(searchQuery: String) =
        RetrofitInstance.api.SearchForNews(searchQuery)

}