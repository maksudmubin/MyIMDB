package com.mubin.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a movie stored in the local database for the MyIMDB app.
 *
 * This entity corresponds to the "movies" table in the Room database.
 * It is designed to store all necessary details about a movie, including
 * metadata, genres, and wishlist status.
 *
 * @property id Unique identifier for the movie. Acts as the primary key.
 * @property title The title of the movie.
 * @property year The release year of the movie (stored as a string for flexibility).
 * @property runtime The duration of the movie (e.g., "120 min").
 * @property director The name of the movie's director.
 * @property actors Comma-separated list of main actors in the movie.
 * @property plot A short description or plot summary of the movie.
 * @property posterUrl The URL to the movie's poster image.
 * @property genres List of genres for the movie (stored using a [androidx.room.TypeConverter] in Room).
 * @property isInWishlist Flag indicating whether the movie is in the user's wishlist.
 *
 * ### Notes:
 * - Genres are stored as a list, but Room persists them as a single string via a [androidx.room.TypeConverter].
 * - `isInWishlist` is `false` by default for new entries.
 */
@Entity(tableName = "movies")
data class MovieEntity(

    /** Unique movie ID — primary key in the database. */
    @PrimaryKey val id: Int,

    /** Movie title. */
    val title: String,

    /** Release year as a string (can include formats like "2023" or "2023–"). */
    val year: String,

    /** Runtime in minutes (stored as a formatted string, e.g., "150 min"). */
    val runtime: String,

    /** Director of the movie. */
    val director: String,

    /** Comma-separated list of main actors. */
    val actors: String,

    /** Plot summary or description. */
    val plot: String,

    /** URL pointing to the movie's poster image. */
    val posterUrl: String,

    /**
     * List of genres for the movie.
     * Stored in the database via a custom [androidx.room.TypeConverter] as a single string.
     */
    val genres: List<String>,

    /**
     * Indicates whether the movie is marked as a favorite/wishlist item.
     * Defaults to `false` for new entries.
     */
    val isInWishlist: Boolean = false
)