package com.bootcamp.soccernews.data.model


import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "news"
)
data class News(
    var id: Int? = null,
    val tittle: String,
    val description: String,
    val imageUrl: String,
    val newsUrl: String,
    val date: String
)