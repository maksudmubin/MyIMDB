package com.mubin.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mubin.common.utils.logger.MyImdbLogger

/**
 * Represents a movie genre stored in the local Room database for the MyIMDB app.
 *
 * This entity corresponds to the "genres" table in the database and stores
 * each genre as a single row with its name as the primary key.
 *
 * @property name The unique name of the genre (e.g., "Action", "Drama").
 */
@Entity(tableName = "genres")
data class GenreEntity(

    /** The unique name of the genre — acts as the primary key. */
    @PrimaryKey val name: String
) {
    init {
        // Log creation of a GenreEntity instance for debugging purposes
        MyImdbLogger.d("GenreEntity", "GenreEntity created with name: \"$name\"")
    }
}