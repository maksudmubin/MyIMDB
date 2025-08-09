package com.mubin.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.database.entity.GenreEntity

/**
 * Data Access Object (DAO) for managing movie genres in the MyIMDB app.
 *
 * This interface defines database operations for the "genres" table.
 * All methods are marked as `suspend` so they can be safely called
 * from coroutines without blocking the main thread.
 */
@Dao
interface GenreDao {

    /**
     * Inserts a list of genres into the database.
     * If a genre already exists (same [GenreEntity.name]), it will be replaced.
     *
     * @param genres List of genres to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(genres: List<GenreEntity>) {
        MyImdbLogger.d("GenreDao", "Inserting ${genres.size} genres into database.")
    }

    /**
     * Retrieves all genres from the database.
     *
     * @return List of [GenreEntity] representing all stored genres.
     */
    @Query("SELECT * FROM genres")
    suspend fun getAllGenres(): List<GenreEntity>
}