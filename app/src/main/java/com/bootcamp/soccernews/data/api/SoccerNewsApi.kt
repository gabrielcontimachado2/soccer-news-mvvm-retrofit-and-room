package com.bootcamp.soccernews.data.api


import com.bootcamp.soccernews.data.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
interface SoccerNewsApi {

    @GET("news.json")
    suspend fun getAllNews(): Response<NewsResponse>

}