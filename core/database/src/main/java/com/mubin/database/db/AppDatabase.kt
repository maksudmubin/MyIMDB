package com.mubin.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mubin.database.dao.GenreDao
import com.mubin.database.dao.MovieDao
import com.mubin.database.entity.GenreEntity
import com.mubin.database.entity.MovieEntity
import com.mubin.database.util.Converters

@Database(
    entities = [MovieEntity::class, GenreEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun genreDao(): GenreDao
}