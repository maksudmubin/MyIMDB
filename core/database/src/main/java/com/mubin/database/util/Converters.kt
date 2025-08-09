package com.mubin.database.util

import androidx.room.TypeConverter
import com.mubin.common.utils.logger.MyImdbLogger

/**
 * Provides custom type conversion methods for Room database.
 *
 * Room can only persist primitive data types and certain supported classes.
 * This class handles the conversion between [List]<[String]> and [String] for storing genres
 * in the database, since Room does not natively support storing lists.
 *
 * - `fromList()` converts a list of genres into a comma-separated string for storage.
 * - `toList()` converts a comma-separated string back into a list of genres.
 */
class Converters {

    /**
     * Converts a list of genres into a comma-separated string.
     *
     * @param genres The list of genre strings to convert.
     * @return A single string containing genres separated by commas.
     */
    @TypeConverter
    fun fromList(genres: List<String>): String {
        val result = genres.joinToString(",")
        // Log the conversion for debugging purposes
        MyImdbLogger.d("Converters", "Converting List to String: $genres -> \"$result\"")
        return result
    }

    /**
     * Converts a comma-separated string of genres into a list.
     *
     * @param data The string containing genres separated by commas.
     * @return A list of genre strings, trimmed of whitespace.
     */
    @TypeConverter
    fun toList(data: String): List<String> {
        val result = data.split(",").map { it.trim() }
        // Log the conversion for debugging purposes
        MyImdbLogger.d("Converters", "Converting String to List: \"$data\" -> $result")
        return result
    }
}