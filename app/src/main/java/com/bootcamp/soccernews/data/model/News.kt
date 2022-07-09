package com.bootcamp.soccernews.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "news"
)
data class News(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?,
    val source: Source?,
) : Serializable