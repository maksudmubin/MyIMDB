package com.mubin.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val year: String,
    val runtime: String,
    val director: String,
    val actors: String,
    val plot: String,
    val posterUrl: String,
    val genres: List<String>, // Stored via TypeConverter
    val isInWishlist: Boolean = false
)