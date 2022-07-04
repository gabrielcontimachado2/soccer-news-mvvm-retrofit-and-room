package com.bootcamp.soccernews.data.repository

import com.bootcamp.soccernews.data.api.RetrofitInstance
import com.bootcamp.soccernews.data.database.local.NewsDataBase

class NewsRepository(
   //val dataBase: NewsDataBase
) {

    suspend fun getAllNews() =
        RetrofitInstance.api.getAllNews()


}