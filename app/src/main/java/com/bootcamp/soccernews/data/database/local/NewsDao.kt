package com.bootcamp.soccernews.data.database.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bootcamp.soccernews.data.model.News

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert( news: News): Long

    @Query("SELECT * FROM news")
    fun getAllNews(): LiveData<List<News>>

    @Delete
    suspend fun deleteNews(news: News)

}