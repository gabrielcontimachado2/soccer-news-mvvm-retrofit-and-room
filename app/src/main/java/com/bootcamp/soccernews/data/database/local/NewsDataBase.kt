package com.bootcamp.soccernews.data.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bootcamp.soccernews.data.model.News


@Database(
    entities = [News::class],
    version = 1
)
abstract class NewsDataBase : RoomDatabase() {

    abstract fun getNewsDao(): NewsDao

    companion object {
        @Volatile
        private var instance: NewsDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                NewsDataBase::class.java,
                "soccer_news_db.db"
            ).build()

    }

}