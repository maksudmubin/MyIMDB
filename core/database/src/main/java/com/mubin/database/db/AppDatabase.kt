package com.mubin.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.database.dao.GenreDao
import com.mubin.database.dao.MovieDao
import com.mubin.database.entity.GenreEntity
import com.mubin.database.entity.MovieEntity
import com.mubin.database.util.Converters

/**
 * Main Room database class for the MyIMDB app.
 *
 * This database holds two tables:
 * - **movies** → managed via [MovieDao]
 * - **genres** → managed via [GenreDao]
 *
 * It also uses [Converters] to handle custom type conversions,
 * such as converting between `List<String>` and `String` for genres.
 *
 * @see MovieEntity
 * @see GenreEntity
 * @see MovieDao
 * @see GenreDao
 */
@Database(
    entities = [MovieEntity::class, GenreEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    init {
        // Log database creation for debugging purposes
        MyImdbLogger.d("AppDatabase", "AppDatabase instance created.")
    }

    /** Provides access to movie-related database operations. */
    abstract fun movieDao(): MovieDao

    /** Provides access to genre-related database operations. */
    abstract fun genreDao(): GenreDao
}