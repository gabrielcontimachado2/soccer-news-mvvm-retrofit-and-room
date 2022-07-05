package com.bootcamp.soccernews.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class NewsResponse(
    @SerializedName("articles")
    val news: List<News>,
    val status: String,
    val totalResults: Int
): Serializable