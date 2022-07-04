package com.bootcamp.soccernews.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class NewsResponse(
    @SerializedName("news")
    val news: List<News>
): Serializable