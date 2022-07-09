package com.bootcamp.soccernews.data.database.local

import androidx.room.TypeConverter
import com.bootcamp.soccernews.data.model.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String? {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }


}