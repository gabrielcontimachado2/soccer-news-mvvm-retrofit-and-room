package com.bootcamp.soccernews.data.api


import com.bootcamp.soccernews.data.model.NewsResponse
import com.bootcamp.soccernews.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getGeneralNewsBrasil(
        @Query("country")
        country: String = "br",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY,
        @Query("category")
        category: String = "general"

    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun getFootballNews(
        @Query("q")
        searchQuery: String = "Football women",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

}