package com.mubin.database.util

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(genres: List<String>): String = genres.joinToString(",")

    @TypeConverter
    fun toList(data: String): List<String> = data.split(",").map { it.trim() }
}